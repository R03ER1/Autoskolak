# Alex — výrazy lva (PNG)

Aplikace načítá první existující soubor z řady kandidátů (viz `AlexAssetResolver` v modulu `app`).

## Doporučené nové soubory (volitelné)

| Soubor | Účel |
|--------|------|
| `AlexSadC.png` | Nálada „hladový“ (HUNGRY) |
| `AlexFamine.png` | Nálada „velmi hladový“ (STARVING) |
| `CAlexSadC.png` | HUNGRY + zapnuté brýle |
| `CAlexFamine.png` | STARVING + zapnuté brýle |

Bez nich se použije fallback (`AlexSad` / `AlexHungry` atd.).
