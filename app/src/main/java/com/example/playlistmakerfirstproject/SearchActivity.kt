package com.example.playlistmakerfirstproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.hideKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var linearNothingFound: LinearLayout
    private lateinit var linearNoInternet: LinearLayout

    private lateinit var historySearchGroup: ConstraintLayout
    private lateinit var updateButton: Button
    private lateinit var clearButton: Button
    private lateinit var cleanHistoryButton: Button

    private lateinit var recycler: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView

    private lateinit var returnItemImageView: ImageView

    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView

    private var lastRequest: String = ""
    private var inputSaveText: String = ""
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit =
        Retrofit.Builder().baseUrl(itunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    private val itunesService = retrofit.create(ApiForItunes::class.java)

    private var trackList = ArrayList<Track>()
    private var adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()
    private lateinit var sharedPrefs: SharedPreferences


    private lateinit var searchHistory: SearchHistory

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        findItems()
        recyclerSetting()
        addChangeListeners()

        sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        // Кнопка очищения истории поиска
        cleanHistoryButton.setOnClickListener {
            sharedPrefs.edit().clear().apply()
            historySearchGroup.isVisible = false
        }

        // Реагирует на смену фокуса в EditText
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            historySearchGroup.isVisible =
                hasFocus && inputEditText.text.isEmpty() && searchHistory.get().isNotEmpty()
        }

        // Реагирует на ввод текста в EditText
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                historySearchGroup.visibility =
                    if (inputEditText.hasFocus() && p0?.isEmpty() == true && searchHistory.get()
                            .isNotEmpty()
                    ) View.VISIBLE else View.GONE
                recycler.visibility = if (inputEditText.hasFocus() && p0?.isEmpty() == true) {
                    trackList.clear()
                    adapter.notifyDataSetChanged()
                    View.GONE
                } else View.VISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        adapter.trackList = trackList
        historyAdapter.trackList = searchHistory.get().toCollection(ArrayList())

        inputSaveText = inputEditText.text.toString()

        // Реагирует на нажатие песни в поиске
        adapter.itemClickListener = { _, track ->
            searchHistory.add(track)
        }
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, inputSaveText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputEditText = findViewById<EditText>(R.id.search_content)
        val text = savedInstanceState.getString(PRODUCT_AMOUNT)
        if (!text.isNullOrEmpty()) {
            inputEditText.setText(text)
        }
    }

    private fun search(lastText: String) {
        itunesService.search(lastText).enqueue(object : Callback<ItunesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ItunesResponse>, response: Response<ItunesResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.clear()
                            trackList.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                            recycler.isVisible = true
                            linearNothingFound.isVisible = false
                            linearNoInternet.isVisible = false
                        } else {
                            linearNothingFound.isVisible = true
                            linearNoInternet.isVisible = false
                            recycler.isVisible = false
                            historySearchGroup.isVisible = false
                        }
                    }

                    else -> {
                        linearNoInternet.isVisible = true
                        linearNothingFound.isVisible = false
                        recycler.isVisible = false
                        historySearchGroup.isVisible = false
                    }
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                linearNoInternet.visibility = View.VISIBLE
                linearNothingFound.visibility = View.GONE
                recycler.visibility = View.GONE
            }
        })
    }

    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    private fun addButtonListeners() {
        // Кнопка обновить запрос, когда нет сети
        updateButton.setOnClickListener {
            search(lastRequest)
        }
        // Кнопка назад
        returnItemImageView.setOnClickListener {
            finish()
        }
        // Кнопка очистить поле ввода
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(currentFocus ?: View(this))
            trackList.clear()
            adapter.notifyDataSetChanged()
            linearNothingFound.isVisible = false
            linearNoInternet.isVisible = false

        }
    }

    private fun addChangeListeners() {
        // Реагирует на нажатие кнопки на клавиатуре и выполняет поиск
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(lastText = inputEditText.text.toString())
                lastRequest = inputEditText.text.toString()
                true
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                inputSaveText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        // Реагирует на ввод текста в EditText для отображения кнопок
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun findItems() {
        linearNothingFound = findViewById(R.id.error_block_nothing_found)
        linearNoInternet = findViewById(R.id.error_block_setting)
        clearButton = findViewById<Button>(R.id.exit)
        updateButton = findViewById(R.id.button_update)
        returnItemImageView = findViewById<ImageView>(R.id.return_n)
        cleanHistoryButton = findViewById(R.id.clean_history_button)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        inputEditText = findViewById(R.id.search_content)
        historySearchGroup = findViewById(R.id.history_search_group)
        recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerViewHistory = findViewById<RecyclerView>(R.id.recycler_view_history)
    }

    private fun recyclerSetting() {
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = historyAdapter
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == KEY_LIST_TRACKS) {
            historyAdapter.trackList = searchHistory.get().toCollection(ArrayList())
            historyAdapter.notifyDataSetChanged()
        }
    }
    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val HISTORY_SEARCH = "HISTORY_SEARCH"
        const val KEY_LIST_TRACKS = "tracks_history"
    }
}