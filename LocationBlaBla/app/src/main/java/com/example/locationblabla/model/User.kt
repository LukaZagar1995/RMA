package com.example.locationblabla.model

import com.example.locationblabla.Constants.USER_DEFAULT_IMAGE

data class User(var id: String = "", var username: String = "",
                var profileImage: String = USER_DEFAULT_IMAGE,
                var email: String = "", var status: String = "")