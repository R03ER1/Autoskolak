package cz.autokolk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoModuleRegistryTest {

    @Test
    fun filenameToModule_coversKnownLessonVideos() {
        val map = VideoModuleRegistry.filenameToModule()
        assertEquals(55, map.size)
        assertEquals("videoassets1", map["0494.mp4"])
        assertEquals("videoassets5", map["0983.mp4"])
    }

    @Test
    fun moduleNames_listsFiveFeatureModules() {
        assertEquals(5, VideoModuleRegistry.MODULE_NAMES.size)
        assertTrue(VideoModuleRegistry.MODULE_NAMES.contains("videoassets3"))
    }
}
