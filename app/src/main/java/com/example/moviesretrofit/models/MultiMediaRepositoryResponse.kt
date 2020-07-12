package com.example.moviesretrofit.models

data class MultiMediaRepositoryResponse(val multimediaList: List<MultiMedia>,
                                        val currentPage: Int,
                                        val totalPages: Int)