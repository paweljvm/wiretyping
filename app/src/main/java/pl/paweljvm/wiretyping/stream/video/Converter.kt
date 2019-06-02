package pl.paweljvm.wiretyping.stream.video

import android.graphics.*
import android.hardware.Camera
import java.io.ByteArrayOutputStream


/**
 * Created by pawel on 2019-06-02.
 */
object Converter {
    fun toJPEG(bytes:ByteArray,camera:Camera):ByteArray {
        val parameters = camera.parameters
        val size = parameters.previewSize
        val image =  YuvImage(bytes, ImageFormat.NV21,
                size.width, size.height, null)
        val rectangle = Rect()
        rectangle.bottom =size.height
        rectangle.top = 0
        rectangle.left = 0
        rectangle.right = size.width
        val output = ByteArrayOutputStream();
        image.compressToJpeg(rectangle, 50, output)
        return output.toByteArray()
    }
}