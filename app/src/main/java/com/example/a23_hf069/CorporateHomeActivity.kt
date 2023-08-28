package com.example.a23_hf069

import HomeFragment
import ResumeFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class CorporateHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_corporate_home)

        // 기본 툴바 숨기기
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }

        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var bnv_main = findViewById(R.id.bnv_main) as BottomNavigationView

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리

        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) { // 공고등록
                R.id.jobPostingMenu -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val JobPostingFragment = JobPostingFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, JobPostingFragment).commit()
                }
                R.id.jobManagementMenu -> { // 공고관리
                    val JobManagementFragment = JobManagementFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, JobManagementFragment).commit()
                }
                R.id.talentManagementMenu -> { // 인재관리
                    val TalentManagementFragment = TalentManagementFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, TalentManagementFragment).commit()
                }
                R.id.mypageMenu -> { // 마이페이지
                    val CorporateMypageFragment = CorporateMypageFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, CorporateMypageFragment).commit()
                }
            }
            true
        }
            bnv_main.selectedItemId = R.id.jobPostingMenu
        }
    }
}