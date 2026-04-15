package cz.autokolk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoModuleRegistryTest {

    @Test
    fun filenameToModule_coversKnownLessonVideos() {
        val map = VideoModuleRegistry.filenameToModule()
        assertEquals(55, map.size)
        assertEquals(VideoModuleRegistry.MEDIA_FEATURE_MODULE_NAME, map["0494.mp4"])
        assertEquals(VideoModuleRegistry.MEDIA_FEATURE_MODULE_NAME, map["0983.mp4"])
    }

    @Test
    fun moduleNames_singleFeatureModule() {
        assertEquals(1, VideoModuleRegistry.MODULE_NAMES.size)
        assertTrue(VideoModuleRegistry.MODULE_NAMES.contains(VideoModuleRegistry.MEDIA_FEATURE_MODULE_NAME))
    }
}
