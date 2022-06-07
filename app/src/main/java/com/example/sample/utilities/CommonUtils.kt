package v2.ltd.nc_retailer.utilities

import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.util.DisplayMetrics
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import v2.ltd.nc_retailer.utilities.CommonUtils
import java.io.*
import java.lang.StringBuilder
import android.graphics.BitmapFactory
import com.example.sample.R


class CommonUtils(var context: Context) {
    fun getResizedBitmap(bm: Bitmap?, newWidth: Int, newHeight: Int): Bitmap {
        var bm = bm
        val width = bm!!.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        if (bm != null && !bm.isRecycled) {
            bm.recycle()
            bm = null
        }
        return resizedBitmap
    }

    fun compressImage(absolutePath: String, imageName: String): String {
        var scaledBitmap: Bitmap? = null
        var options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
       // options = BitmapFactory.Options()
        options.inSampleSize = 2
        var bmp = BitmapFactory.decodeFile(
            absolutePath,  options // getRealPathFromURI(imageUri);, options
        )
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 1200.0f
        val maxWidth = 950.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(
                absolutePath // getRealPathFromURI(imageUri);, options
            )
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(
                absolutePath // getRealPathFromURI(imageUri);
            )
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = getFilename(imageName)
        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getFilename(imageName: String): String {
        val file = File(context.externalCacheDir, "outlet_images")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + imageName + ".jpg"
    }

    fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        val path = fileUri.path
        Log.d("pathimg", path!!)
        val file = File(path)
        val requestFile = RequestBody.create("signature".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    val isTablet: Boolean
        get() {
            val xlarge =
                context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_XLARGE
            val large =
                context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
            return xlarge || large
        }// Yes, this is a tablet!


    val isTabletDevice: Boolean
        // If XLarge, checks if the Generalized Density is at least MDPI
        // (160dpi)
        get() {
            // Verifies if the Generalized Size of the device is XLARGE to be
            // considered a Tablet
            val xlarge = context.resources.configuration.screenLayout and
                    Configuration.SCREENLAYOUT_SIZE_MASK ==
                    Configuration.SCREENLAYOUT_SIZE_XLARGE

            // If XLarge, checks if the Generalized Density is at least MDPI
            // (160dpi)
            if (xlarge) {
                val metrics = DisplayMetrics()
                val activity = context as Activity
                activity.windowManager.defaultDisplay.getMetrics(metrics)

                // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
                // DENSITY_TV=213, DENSITY_XHIGH=320
                if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM || metrics.densityDpi == DisplayMetrics.DENSITY_TV || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

                    // Yes, this is a tablet!
                    return true
                }
            }

            // No, this is not a tablet!
            return false
        }

    fun setIconColor(color: Int): BitmapDrawable? {
        var color = color
        if (color == 0) {
            color = -0x1
        }
        val res = context.resources
        val maskDrawable = res.getDrawable(R.drawable.ic_sync_img) as? BitmapDrawable ?: return null
        val maskBitmap = maskDrawable.bitmap
        val width = maskBitmap.width
        val height = maskBitmap.height
        val outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outBitmap)
        canvas.drawBitmap(maskBitmap, 0f, 0f, null)
        val maskedPaint = Paint()
        maskedPaint.color = color
        maskedPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskedPaint)
        return BitmapDrawable(res, outBitmap)
    } //    public void loadImage(String url, ImageView imageView){

    //        Glide.with(context)
    //                .load(url+"")
    //                .centerCrop()
    //                .placeholder(R.drawable.background_img)
    //                .error(R.drawable.background_img)
    //                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    //                .into(imageView);
    //
    //    }
    companion object {
        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var dist = (Math.sin(deg2rad(lat1))
                    * Math.sin(deg2rad(lat2))
                    + (Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta))))
            dist = Math.acos(dist)
            dist = rad2deg(dist)
            dist = dist * 60 * 1.1515 * 1.609344
            return dist
        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap? {
            var bitmap: Bitmap? = null
            if (drawable is BitmapDrawable) {
                val bitmapDrawable = drawable
                if (bitmapDrawable.bitmap != null) {
                    return bitmapDrawable.bitmap
                }
            }
            bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun readTextfromfile(gFile: File?): String {
            val textfromfile = StringBuilder()
            try {
                val br = BufferedReader(FileReader(gFile))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    textfromfile.append(line)
                    //textfromfile.append('\n');
                }
                br.close()
            } catch (e: IOException) {
                //You'll need to add proper error handling here
            }
            return textfromfile.toString()
        }
    }


}