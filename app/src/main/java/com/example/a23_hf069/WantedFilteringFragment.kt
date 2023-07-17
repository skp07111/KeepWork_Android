package com.example.a23_hf069

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import kotlinx.android.synthetic.main.fragment_wanted_work_net.*


class WantedFilteringFragment : Fragment()  {
    lateinit var filter: Button
    private lateinit var jobListView: ListView
    private lateinit var jobList: List<Job>
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private var currentPage = 1
    // 화면 띄우기
    override fun onCreateView( // onCreateView 함수 오버라이드 해줌
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View?
    {
        val view= inflater.inflate(R.layout.fragment_wanted_filtering, container, false)
        val filtered= inflater.inflate(R.layout.fragment_wanted_work_net, container, false)

        // UI 요소 초기화
        jobListView = view.findViewById(R.id.jobListView)
        prevButton = view.findViewById(R.id.prevButton)
        nextButton = view.findViewById(R.id.nextButton)

        // 이전 페이지 버튼 클릭 이벤트 처리
        prevButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage -= 1
                //fetchJobData()
            }
        }

        // 다음 페이지 버튼 클릭 이벤트 처리
        nextButton.setOnClickListener {
            currentPage += 1
            //fetchJobData()
        }

        val button = view.findViewById<Button>(R.id.complete_btn1)
        button.setOnClickListener {
            // 다른 XML로 이동하는 코드 작성
            val fragment2 = WantedFilteredFragment() // 이동할 프래그먼트
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, fragment2) // 레이아웃에 프래그먼트 교체
                .addToBackStack(null) // 백 스택에 추가하여 이전 프래그먼트로 돌아갈 수 있게 함
                .commit()
        }
        return view
    }
}

