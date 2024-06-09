package com.example.digfrige;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<String[]> data;  // 전체 데이터
    private List<String> titles;  // 현재 표시되는 제목 리스트
    private final int visibleThreshold = 50;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        webView = findViewById(R.id.webView);

        // WebView 설정
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // CSV 파일 읽기 및 데이터 처리
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.grouped_recipes);
            data = CSVReader.readCSV(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error reading CSV file", e);
            return;
        }


        // 데이터가 null인지 확인
        if (data == null) {
            Log.e("MainActivity", "Data is null");
            return;
        }

        // 전체 데이터에서 제목만 추출하여 리스트업 (두 번째 행부터)
        titles = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            titles.add(row[0]); // 두 번째 행부터 제목 추가
        }

        adapter = new ItemAdapter(titles, data);
        recyclerView.setAdapter(adapter);

        // 검색 입력 필드 및 버튼 설정
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                search();
                return true;
            }
            return false;
        });

        // 아이템 클릭 이벤트 설정
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String url) {
                // WebView에 URL 로드
                recyclerView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(url);
            }
        });

    }

    // 검색어를 기준으로 필터링하여 재료를 찾는 메서드
    private void filter(String query) {
        if (query.length() < 1) {
            // 한 글자 미만의 검색어는 무시
            return;
        }
        List<String> filteredTitles = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase(); // 대소문자 구분 없이 검색하기 위해 검색어를 소문자로 변환
        for (String[] row : data) {
            if (row[2].toLowerCase().contains(lowercaseQuery)) {
                filteredTitles.add(row[0]);
            }
        }
        adapter.updateList(filteredTitles);
    }

    // 검색 실행
    private void search() {
        EditText searchEditText = findViewById(R.id.searchEditText);
        String query = searchEditText.getText().toString().trim(); // 검색어의 앞뒤 공백 제거
        filter(query);
    }

    // 뒤로 가기 버튼을 눌렀을 때 동작 설정
    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE) {
            // 웹뷰가 보이는 상태에서 뒤로가기를 누르면 웹뷰를 숨깁니다.
            webView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
