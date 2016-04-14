package com.rahul.mymusic.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rahul.mymusic.Helper.Constants;
import com.rahul.mymusic.R;
import com.rahul.mymusic.YoutubeSongsListAsyncTask;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class YoutubeSearchFragment extends android.support.v4.app.Fragment {
    private AutoCompleteTextView searchBox;
    Button submitButton;
    ArrayAdapter<String> arrayAdapter;
    MyTextWatcher textWatcher;
    SearchSuggestionAsyncTask myAsyncTask;
    public RecyclerView recyclerView;
    boolean doubleBackToExitPressedOnce = false;
    public ProgressBar progressBar, suggestionProgressBar;
    public TextView countTV;
    ImageView clearButton;
    View rootView;

    public static YoutubeSearchFragment newInstance() {
        YoutubeSearchFragment fragment = new YoutubeSearchFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public YoutubeSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rootView = view;
        initializeView(view);
        textWatcher = new MyTextWatcher();
        searchBox.addTextChangedListener(textWatcher);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
                searchBox.requestFocus();
            }
        });
        searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View focus = getActivity().getCurrentFocus();
                if (focus != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                }
                searchBox.clearFocus();
                submitButton.requestFocus();
                submitButton.performClick();
            }
        });

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    submitButton.performClick();
                    return true;
                }
                return false;
            }
        });

        searchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchBox.addTextChangedListener(textWatcher);
                } else {
                    searchBox.removeTextChangedListener(textWatcher);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchBox.getText().toString().isEmpty()) {
                    View focus = getActivity().getCurrentFocus();
                    if (focus != null) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                    }
                    countTV.setVisibility(View.GONE);
                    YoutubeSongsListAsyncTask songsListAsyncTask = new YoutubeSongsListAsyncTask(getContext(), rootView);
                    songsListAsyncTask.execute(searchBox.getText().toString());
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeView(View view) {
        submitButton = (Button) view.findViewById(R.id.submit_button);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        suggestionProgressBar = (ProgressBar) view.findViewById(R.id.suggestion_progress);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        countTV = (TextView) view.findViewById(R.id.item_count);
        clearButton = (ImageView) view.findViewById(R.id.clear_button);
        searchBox = (AutoCompleteTextView) view.findViewById(R.id.search_box);
    }

    public class SearchSuggestionAsyncTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            suggestionProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            Document document = null;
            List<String> list = new ArrayList<>();
            try {
                document = Jsoup.connect(Constants.SEARCH_QUERY_PREFIX + params[0])
                        .timeout(0)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                        .header("Content-Type", "application/json")
                        .get();

                String jsonString = document.text().substring(document.text().indexOf("(")+1);
                jsonString = jsonString.replace(")", "");
                JSONArray jsonArray = new JSONArray(jsonString);
                jsonArray = jsonArray.getJSONArray(1);
                for(int i=0; i<jsonArray.length(); ++i){
                    list.add(jsonArray.getJSONArray(i).getString(0));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            arrayAdapter = new ArrayAdapter<String>(getContext(),
                    R.layout.row,list);
            searchBox.setAdapter(arrayAdapter);
            if (searchBox.hasFocus())
                searchBox.showDropDown();
            suggestionProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 2 && searchBox.hasFocus()) {
                if (myAsyncTask != null)
                    myAsyncTask.cancel(true);
                myAsyncTask = new SearchSuggestionAsyncTask();
                myAsyncTask.execute(s.toString().trim().replace(" ", "+"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
