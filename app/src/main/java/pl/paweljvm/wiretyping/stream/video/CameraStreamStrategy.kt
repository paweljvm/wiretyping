package pl.paweljvm.wiretyping.stream.video

import android.graphics.ImageFormat
import android.hardware.Camera
import pl.paweljvm.wiretyping.stream.StreamStrategy

/**
 * Created by pawel on 2019-06-02.
 */
object CameraStreamStrategy : StreamStrategy {

    private var camera:Camera? = null

    override fun handleStart(newBufferListener: (size: Int, buffer: ByteArray) -> Unit) {
        camera = Camera.open()
        val params = camera?.parameters
        params?.pictureFormat=ImageFormat.JPEG
        params?.setPreviewSize(640,480)
        params?.setPreviewFpsRange(30000,30000)
        params?.removeGpsData()
        camera?.parameters = params
        camera?.startPreview()
        camera?.setPreviewCallback { data:ByteArray, camera:Camera ->
            val compressed = Converter.toJPEG(data, camera)
            newBufferListener(compressed.size,compressed)
        }
    }

    override fun handleStop() {
        camera?.let { it.release() }
        camera = null
    }

}