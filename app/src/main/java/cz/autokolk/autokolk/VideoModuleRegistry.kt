package cz.autokolk

/**
 * Maps lesson video filenames to Play Feature Delivery module names.
 * All media (images + lesson videos) lives in a single on-demand module under the Play **200 MB per feature module** limit.
 */
object VideoModuleRegistry {

    const val MEDIA_FEATURE_MODULE_NAME = "mediaassets"

    val MODULE_NAMES = listOf(MEDIA_FEATURE_MODULE_NAME)

    private val VIDEO_FILENAMES = listOf(
        "0494.mp4", "0679.mp4", "0032.mp4", "0561.mp4", "0648.mp4", "0563.mp4",
        "0657.mp4", "0054.mp4", "0669.mp4", "0118.mp4", "0021.mp4",
        "0380.mp4", "0063.mp4", "0553.mp4", "0658.mp4", "0465.mp4", "0663.mp4",
        "0661.mp4", "0055.mp4", "0551.mp4", "0665.mp4", "0548.mp4",
        "0957.mp4", "0564.mp4", "0562.mp4", "0350.mp4", "0672.mp4", "0057.mp4",
        "0033.mp4", "0655.mp4", "0117.mp4", "0660.mp4", "0348.mp4",
        "0673.mp4", "0674.mp4", "0464.mp4", "0463.mp4", "0128.mp4", "0999.mp4",
        "0347.mp4", "0662.mp4", "0664.mp4", "0666.mp4", "0048.mp4",
        "0554.mp4", "0056.mp4", "0115.mp4", "0493.mp4", "0659.mp4", "0656.mp4",
        "0915.mp4", "0670.mp4", "0667.mp4", "0668.mp4", "0983.mp4",
    )

    fun filenameToModule(): Map<String, String> =
        VIDEO_FILENAMES.associateWith { MEDIA_FEATURE_MODULE_NAME }
}
