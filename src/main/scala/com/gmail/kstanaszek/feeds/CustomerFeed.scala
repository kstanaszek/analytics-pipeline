package com.gmail.kstanaszek.feeds

case class CustomerFeed(link: String,
                        title: String,
                        id: String,
                        imageLink: String,
                        description: String,
                        googleProductCategory: String,
                        price: Double,
                        additionalImageLink: String,
                        shipping: Double,
                        availability: String)
