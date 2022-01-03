/*
 * Copyright (C) 2022 Google Developer Student Clubs BBSBEC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdsc.bbsbec.booksapi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gdsc.bbsbec.booksapi.viewmodel.MainViewModel
import com.gdsc.bbsbec.booksapi.viewmodel.MainViewModelFactory
import com.gdsc.bbsbec.booksapi.databinding.ActivityMainBinding
import com.gdsc.bbsbec.booksapi.repository.Repository
import com.gdsc.bbsbec.booksapi.utils.Constants.Companion.API_KEY

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bookName = ArrayList<String>()
        val bookPublisher = ArrayList<String>()
        val bookSmallThumbnail = ArrayList<String>()

        val bookThumbnail = ArrayList<String>()
        val bookDescription = ArrayList<String>()
        val previewLink = ArrayList<String>()

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.searchButton.setOnClickListener {
            val title: String = binding.searchBookTextInput.editText?.text.toString()
            viewModel.getBooks(title, API_KEY)
            viewModel.myResponse.observe(this, Observer { response ->
                if (response.isSuccessful) {
                    val intent = Intent(this, BookSearchResultRecyclerView::class.java)

                    response.body()!!.items.forEach {
                        bookName.add(it.volumeInfo!!.title.toString())
                        bookPublisher.add(it.volumeInfo!!.publisher.toString())
                        bookSmallThumbnail.add(it.volumeInfo!!.imageLinks!!.smallThumbnail.toString())
                        bookThumbnail.add(it.volumeInfo!!.imageLinks!!.thumbnail.toString())
                        bookDescription.add(it.volumeInfo!!.description.toString())
                        previewLink.add(it.volumeInfo!!.previewLink.toString())
                    }
                    intent.putExtra("bookName", bookName)
                    intent.putExtra("publisher", bookPublisher)
                    intent.putExtra("bookSmallThumbnail", bookSmallThumbnail)
                    intent.putExtra("bookThumbnail", bookThumbnail)
                    intent.putExtra("bookDescription", bookDescription)
                    intent.putExtra("previewLink", previewLink)

                    startActivity(intent)
                    finish()
                } else {
                    Log.d("Response", response.errorBody().toString())
                }
            })
        }
    }
}