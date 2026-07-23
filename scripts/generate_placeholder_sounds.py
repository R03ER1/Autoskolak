"""Generate small placeholder .wav sounds for the Autoškolák app.

All output is procedurally synthesized (sine/triangle/noise + envelopes) —
no external samples. Runs on stdlib only (wave + math + struct + random).

Output goes to `app/src/main/res/raw/` as 22 050 Hz mono 16-bit PCM.
Files are named without hyphens (Android resource-safe).

Run:  python scripts/generate_placeholder_sounds.py
"""

from __future__ import annotations

import math
import os
import random
import struct
import wave
from pathlib import Path
from typing import Callable, Iterable

SAMPLE_RATE = 22_050
BITS_PER_SAMPLE = 16
CHANNELS = 1

OUT_DIR = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "raw"

# ── envelope + oscillator helpers ─────────────────────────────────────────

def _samples(duration_s: float) -> int:
    return int(duration_s * SAMPLE_RATE)


def _env_ar(n: int, attack: float = 0.02, release: float = 0.6) -> Callable[[int], float]:
    a = max(1, int(n * attack))
    r_start = int(n * (1.0 - release))
    def env(i: int) -> float:
        if i < a:
            return i / a
        if i >= r_start:
            span = n - r_start
            if span <= 0:
                return 0.0
            return max(0.0, 1.0 - (i - r_start) / span)
        return 1.0
    return env


def _sine(t: float, freq: float) -> float:
    return math.sin(2 * math.pi * freq * t)


def _triangle(t: float, freq: float) -> float:
    phase = (t * freq) % 1.0
    return 4.0 * abs(phase - 0.5) - 1.0


def _square(t: float, freq: float) -> float:
    return 1.0 if math.sin(2 * math.pi * freq * t) >= 0 else -1.0


def _noise() -> float:
    return random.uniform(-1.0, 1.0)


# ── sound generators (each returns a list[float] in [-1, 1]) ──────────────

def gen_correct() -> list[float]:
    """Short bright chirp — ascending sine from 900 to 1400 Hz, 220 ms."""
    dur = 0.22
    n = _samples(dur)
    env = _env_ar(n, attack=0.02, release=0.6)
    out = [0.0] * n
    for i in range(n):
        t = i / SAMPLE_RATE
        # linear ramp of freq
        f = 900.0 + (1400.0 - 900.0) * (i / n)
        out[i] = 0.55 * env(i) * _sine(t, f)
    return out


def gen_wrong() -> list[float]:
    """Low descending buzz — 250 Hz → 130 Hz, square-ish, 260 ms."""
    dur = 0.26
    n = _samples(dur)
    env = _env_ar(n, attack=0.01, release=0.7)
    out = [0.0] * n
    for i in range(n):
        t = i / SAMPLE_RATE
        f = 250.0 + (130.0 - 250.0) * (i / n)
        # mix square + sine for "buzz" character, keep amplitude modest
        s = 0.6 * _square(t, f) + 0.4 * _sine(t, f * 0.5)
        out[i] = 0.45 * env(i) * s
    return out


def gen_combo() -> list[float]:
    """Two-note ascending chime (C5→G5), 320 ms."""
    dur = 0.32
    n = _samples(dur)
    env = _env_ar(n, attack=0.02, release=0.75)
    out = [0.0] * n
    boundary = n // 2
    for i in range(n):
        t = i / SAMPLE_RATE
        f = 523.25 if i < boundary else 783.99
        out[i] = 0.55 * env(i) * (0.85 * _sine(t, f) + 0.15 * _sine(t, f * 2))
    return out


def gen_countdown() -> list[float]:
    """Short 800 Hz blip, 120 ms."""
    dur = 0.12
    n = _samples(dur)
    env = _env_ar(n, attack=0.01, release=0.7)
    out = [0.0] * n
    for i in range(n):
        t = i / SAMPLE_RATE
        out[i] = 0.55 * env(i) * _sine(t, 800.0)
    return out


def gen_tap() -> list[float]:
    """Very short high-pitched click, ~40 ms, quiet."""
    dur = 0.04
    n = _samples(dur)
    env = _env_ar(n, attack=0.01, release=0.6)
    out = [0.0] * n
    for i in range(n):
        t = i / SAMPLE_RATE
        out[i] = 0.28 * env(i) * _sine(t, 1600.0)
    return out


def gen_whoosh() -> list[float]:
    """Swept low-pass-ish noise, 220 ms."""
    dur = 0.22
    n = _samples(dur)
    # simple triangular envelope
    peak = n // 3
    out = [0.0] * n
    # one-pole low-pass with sweeping cutoff (implemented as running average window)
    prev = 0.0
    for i in range(n):
        raw = _noise()
        # sweep: 0.05 → 0.45 → 0.15
        p = i / n
        alpha = 0.05 + 0.4 * math.sin(math.pi * p)
        y = alpha * raw + (1.0 - alpha) * prev
        prev = y
        # amplitude envelope
        if i <= peak:
            e = i / max(1, peak)
        else:
            e = max(0.0, 1.0 - (i - peak) / max(1, n - peak))
        out[i] = 0.4 * e * y
    return out


def gen_alex_feed() -> list[float]:
    """Crunchy short blip — filtered noise + soft mid tone, 180 ms."""
    dur = 0.18
    n = _samples(dur)
    env = _env_ar(n, attack=0.01, release=0.7)
    out = [0.0] * n
    prev = 0.0
    for i in range(n):
        t = i / SAMPLE_RATE
        n_sample = 0.6 * _noise() + 0.4 * prev
        prev = n_sample
        tone = _sine(t, 420.0)
        out[i] = 0.45 * env(i) * (0.6 * n_sample + 0.4 * tone)
    return out


def gen_alex_tap() -> list[float]:
    """Soft short pet interaction — one warm note, 140 ms."""
    dur = 0.14
    n = _samples(dur)
    env = _env_ar(n, attack=0.02, release=0.7)
    out = [0.0] * n
    for i in range(n):
        t = i / SAMPLE_RATE
        out[i] = 0.4 * env(i) * (0.7 * _sine(t, 660.0) + 0.3 * _sine(t, 990.0))
    return out


def gen_achievement() -> list[float]:
    """3-note ascending arpeggio (C5-E5-G5), 520 ms."""
    dur = 0.52
    n = _samples(dur)
    env = _env_ar(n, attack=0.02, release=0.55)
    out = [0.0] * n
    third = n // 3
    freqs = (523.25, 659.25, 783.99)
    for i in range(n):
        t = i / SAMPLE_RATE
        idx = min(2, i // third)
        f = freqs[idx]
        out[i] = 0.55 * env(i) * (0.75 * _sine(t, f) + 0.25 * _sine(t, f * 2))
    return out


def gen_wheel_tick() -> list[float]:
    """Very short tick for each wheel segment, 35 ms."""
    dur = 0.035
    n = _samples(dur)
    env = _env_ar(n, attack=0.02, release=0.7)
    out = [0.0] * n
    for i in range(n):
        t = i / SAMPLE_RATE
        out[i] = 0.35 * env(i) * _triangle(t, 1200.0)
    return out


def gen_wheel_win() -> list[float]:
    """Cheerful ascending chime, 600 ms."""
    dur = 0.6
    n = _samples(dur)
    env = _env_ar(n, attack=0.02, release=0.65)
    out = [0.0] * n
    # 4 notes: C5, E5, G5, C6
    freqs = (523.25, 659.25, 783.99, 1046.50)
    part = n // 4
    for i in range(n):
        t = i / SAMPLE_RATE
        idx = min(3, i // part)
        f = freqs[idx]
        out[i] = 0.55 * env(i) * (0.8 * _sine(t, f) + 0.2 * _sine(t, f * 2))
    return out


def gen_streak() -> list[float]:
    """Celebratory 4-note fanfare, 720 ms."""
    dur = 0.72
    n = _samples(dur)
    env = _env_ar(n, attack=0.03, release=0.55)
    out = [0.0] * n
    # G4, C5, E5, G5
    freqs = (392.0, 523.25, 659.25, 783.99)
    part = n // 4
    for i in range(n):
        t = i / SAMPLE_RATE
        idx = min(3, i // part)
        f = freqs[idx]
        # richer sound: sine + fifth
        out[i] = 0.55 * env(i) * (0.7 * _sine(t, f) + 0.2 * _sine(t, f * 1.5) + 0.1 * _sine(t, f * 2))
    return out


def gen_coin() -> list[float]:
    """Coin pling — two overlapping high tones, 220 ms."""
    dur = 0.22
    n = _samples(dur)
    env = _env_ar(n, attack=0.005, release=0.75)
    out = [0.0] * n
    for i in range(n):
        t = i / SAMPLE_RATE
        out[i] = 0.5 * env(i) * (0.55 * _sine(t, 1300.0) + 0.45 * _sine(t, 1950.0))
    return out


def gen_levelup() -> list[float]:
    """Level-up: rising 5-note run (C5-D5-E5-G5-C6), 560 ms."""
    dur = 0.56
    n = _samples(dur)
    env = _env_ar(n, attack=0.02, release=0.55)
    out = [0.0] * n
    freqs = (523.25, 587.33, 659.25, 783.99, 1046.50)
    part = n // 5
    for i in range(n):
        t = i / SAMPLE_RATE
        idx = min(4, i // part)
        f = freqs[idx]
        out[i] = 0.55 * env(i) * (0.75 * _sine(t, f) + 0.25 * _sine(t, f * 2))
    return out


# ── writer ────────────────────────────────────────────────────────────────

def _write_wav(path: Path, samples: Iterable[float]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    frames = bytearray()
    for s in samples:
        # clamp + int16
        clamped = max(-1.0, min(1.0, s))
        i = int(clamped * 32_767)
        frames.extend(struct.pack("<h", i))
    with wave.open(str(path), "wb") as w:
        w.setnchannels(CHANNELS)
        w.setsampwidth(BITS_PER_SAMPLE // 8)
        w.setframerate(SAMPLE_RATE)
        w.writeframes(bytes(frames))


SOUNDS: dict[str, Callable[[], list[float]]] = {
    "sound_correct.wav": gen_correct,
    "sound_wrong.wav": gen_wrong,
    "sound_combo.wav": gen_combo,
    "sound_countdown.wav": gen_countdown,
    "sound_tap.wav": gen_tap,
    "sound_whoosh.wav": gen_whoosh,
    "sound_alex_feed.wav": gen_alex_feed,
    "sound_alex_tap.wav": gen_alex_tap,
    "sound_achievement.wav": gen_achievement,
    "sound_wheel_tick.wav": gen_wheel_tick,
    "sound_wheel_win.wav": gen_wheel_win,
    "sound_streak.wav": gen_streak,
    "sound_coin.wav": gen_coin,
    "sound_levelup.wav": gen_levelup,
}


def main() -> None:
    random.seed(0xA07E01)  # reproducibility across runs
    for name, fn in SOUNDS.items():
        samples = fn()
        out = OUT_DIR / name
        _write_wav(out, samples)
        size_kb = os.path.getsize(out) / 1024.0
        print(f"  wrote {name:28s}  {size_kb:6.1f} KB  ({len(samples) / SAMPLE_RATE * 1000:5.0f} ms)")


if __name__ == "__main__":
    main()
