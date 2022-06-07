package com.example.sample.model

import com.google.gson.annotations.SerializedName

data class UploadImageModel(
    @SerializedName("asset_id"          ) var assetId          : String?           = null,
    @SerializedName("public_id"         ) var publicId         : String?           = null,
    @SerializedName("version"           ) var version          : Int?              = null,
    @SerializedName("version_id"        ) var versionId        : String?           = null,
    @SerializedName("signature"         ) var signature        : String?           = null,
    @SerializedName("width"             ) var width            : Int?              = null,
    @SerializedName("height"            ) var height           : Int?              = null,
    @SerializedName("format"            ) var format           : String?           = null,
    @SerializedName("resource_type"     ) var resourceType     : String?           = null,
    @SerializedName("created_at"        ) var createdAt        : String?           = null,
    @SerializedName("tags"              ) var tags             : ArrayList<String> = arrayListOf(),
    @SerializedName("bytes"             ) var bytes            : Int?              = null,
    @SerializedName("type"              ) var type             : String?           = null,
    @SerializedName("etag"              ) var etag             : String?           = null,
    @SerializedName("placeholder"       ) var placeholder      : Boolean?          = null,
    @SerializedName("url"               ) var url              : String?           = null,
    @SerializedName("secure_url"        ) var secureUrl        : String?           = null,
    @SerializedName("folder"            ) var folder           : String?           = null,
    @SerializedName("access_mode"       ) var accessMode       : String?           = null,
    @SerializedName("original_filename" ) var originalFilename : String?           = null
)
