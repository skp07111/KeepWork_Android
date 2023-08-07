package com.example.a23_hf069


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.a23_hf069.*
import okhttp3.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class WantedFilteringFragment : Fragment() {

    private val baseUrl =
        "http://openapi.work.go.kr/opi/opi/opia/wantedApi.do?authKey=WNLJYZLM2VZXTT2TZA9XR2VR1HK&callTp=L&returnType=XML&startPage=1&display=10"
    //완료 버튼
    lateinit var complete_btn: Button
    //지역,직종
    lateinit var regioncl_btn: Button
    lateinit var jobcl_btn: Button
    lateinit var tv_jobcl_selected: TextView
    lateinit var tv_regioncl_selected: TextView
    //학력
    lateinit var cbAllEdu: CheckBox // 학력무관
    lateinit var cbHighEdu: CheckBox // 고졸
    lateinit var cbUniv2: CheckBox // 대졸(2~3년)
    lateinit var cbUniv4: CheckBox // 대졸(4년)
    //경력
    lateinit var cbAllCareer : CheckBox // 경력무관
    lateinit var cbFresh : CheckBox // 신입
    lateinit var cbExperienced : CheckBox // 경력
    //마감일
    lateinit var cbToday : CheckBox//오늘까지
    lateinit var cbTomorrow : CheckBox//내일까지
    lateinit var cb7days : CheckBox//일주일 이내
    lateinit var cb30days : CheckBox//한달 이내
    lateinit var cb60days : CheckBox//두달 이내

    private lateinit var wantedList: List<Wanted>
    private val sharedSelectionViewModel: SharedSelectionViewModel by activityViewModels() // 필터링된 리스트를 전달하는 viewModel 객체 생성
    lateinit var selectedRegion : String
    lateinit var selectedJob : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_wanted_filtering, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //완료 버튼 초기화
        complete_btn = view.findViewById<Button>(R.id.complete_btn1)
        //지역 선택 초기화
        regioncl_btn = view.findViewById<Button>(R.id.regioncl_btn)
        //직종 선택 초기화
        jobcl_btn = view.findViewById<Button>(R.id.jobcl_btn)

        // 선택된 지역 정보를 나타낼 TextView 초기화
        tv_regioncl_selected = view.findViewById(R.id.tv_regioncl_selected)

        // 선택된 직종 정보를 나타낼 TextView 초기화
        tv_jobcl_selected = view.findViewById(R.id.tv_jobcl_selected)

        // ViewModel에서 선택된 지역 정보를 가져와서 TextView에 설정
        selectedRegion = sharedSelectionViewModel.selectedRegion.toString()
        tv_regioncl_selected.text = selectedRegion

        // ViewModel에서 선택된 직종 정보를 가져와서 TextView에 설정
        selectedJob = sharedSelectionViewModel.selectedJob.toString()
        tv_jobcl_selected.text = selectedJob


        // CheckBox 변수들을 초기화
        cbAllCareer = view.findViewById(R.id.cb_c_1)
        cbFresh = view.findViewById(R.id.cb_c_2)
        cbExperienced = view.findViewById(R.id.cb_c_3)

        cbAllEdu = view.findViewById(R.id.cb_e_1)
        cbHighEdu = view.findViewById(R.id.cb_e_4)
        cbUniv2 = view.findViewById(R.id.cb_e_5)
        cbUniv4 = view.findViewById(R.id.cb_e_6)

        cbToday = view.findViewById(R.id.cb_d_2)
        cbTomorrow = view.findViewById(R.id.cb_d_3)
        cb7days = view.findViewById(R.id.cb_d_4)
        cb30days = view.findViewById(R.id.cb_d_5)
        cb60days = view.findViewById(R.id.cb_d_6)

        // 완료 버튼이 눌렸을 때 지역,직종 변수 및 학력,경력,마감일 체크박스 확인 -> 선택된 조건에 해당하는 공고목록 가져와서 UI에 업데이트
        complete_btn.setOnClickListener {


            // 선택한 지역이 있을 경우 필터링하기
            if(selectedRegion != ""){
                fetchWantedList("region","$selectedRegion")
            }
            // 선택한 직종이 있을 경우 필터링하기

            // 학력 중 선택한 체크박스가 있을 경우 필터링하기
            if(cbAllEdu.isChecked){
                fetchWantedList("edu","학력무관")
            }
            if(cbHighEdu.isChecked){
                fetchWantedList("edu","고졸")
            }
            if(cbUniv2.isChecked){
                fetchWantedList("edu","대졸(2~3년)")
            }
            if(cbUniv4.isChecked){
                fetchWantedList("edu","대졸4년")
            }

            // 경력 중 선택한 체크박스가 있을 경우 필터링하기
            if(cbAllCareer.isChecked){
                fetchWantedList("career","관계없음")
            }
            if(cbFresh.isChecked){
                fetchWantedList("career","신입")
            }
            if(cbExperienced.isChecked){
                fetchWantedList("career","경력")
            }

            // 마감일 중 선택한 체크박스가 있을 경우 필터링하기
            if(cbToday.isChecked){
                fetchWantedList("closeDt","today")
            }
            if(cbTomorrow.isChecked){
                fetchWantedList("closeDt","tomorrow")
            }
            if(cb7days.isChecked){
                fetchWantedList("closeDt","7days")
            }
            if(cb30days.isChecked){
                fetchWantedList("closeDt","30days")
            }
            if(cb60days.isChecked){
                fetchWantedList("closeDt","60days")
            }

            // 선택된 조건들을 반영한 sharedSelectionViewModel 속 리스트들을 반영한 리스트뷰 화면으로 전환
            val wantedResultFragment = WantedResultFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, wantedResultFragment)
                .addToBackStack(null)
                .commit()
        } // complete_btn 리스너 종료

        // 지역선택 버튼 눌렸을 때 지역선택 화면으로 전환
        regioncl_btn.setOnClickListener {
            val regionSelectionFragment = RegionSelectionFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, regionSelectionFragment)
                .addToBackStack(null)
                .commit()
        }

        // 직종선택 버튼 눌렸을 때 직종선택 화면으로 전환
        jobcl_btn.setOnClickListener {
            val jobSelectionFragment = JobWorkNetSelectionFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, jobSelectionFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    // 카테고리와 키워드에 해당하는 채용공고 가져와서 sharedSelectionViewModel의 리스트에 저장
    private fun fetchWantedList(category:String?,keyword: String?){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$baseUrl")
            .build()
        var result: List<Wanted> = emptyList()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val xmlString = response.body?.string()
                    result = parseXmlResponse(xmlString) // parsing하기
                    wantedList = result
                    if (category == "region") {
                        for(i in wantedList){
                            if(keyword == i.region){
                                println(i.region)
                                println(i.company)
                                println(i.title)
                                println("______________________________")

                            }
                        }
                        val filteredList = wantedList.filter { it.region == keyword }
                        sharedSelectionViewModel.updateFilteredList(category, filteredList)
                    }
                    // if문 region 종료

//                    else if(category == "job"){
//
//                    }
                    else if (category == "edu") {
                        for(i in wantedList){
                            if(keyword == i.minEdubg){
                                println(i.minEdubg)
                                println(i.company)
                                println(i.title)
                                println("______________________________")

                            }
                        }
                        val filteredList = wantedList.filter { it.minEdubg == keyword }
                        sharedSelectionViewModel.updateFilteredList(category, filteredList)
                    }
                    else if (category == "career") {
                        for(i in wantedList){
                            if(keyword == i.career){
                                println(i.career)
                                println(i.company)
                                println(i.title)
                                println("______________________________")

                            }
                        }
                        val filteredList = wantedList.filter { it.career == keyword }
                        sharedSelectionViewModel.updateFilteredList(category, filteredList)
                    }
                    else if (category == "closeDt") {
                        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd")

                        val today = LocalDate.now()
                        val formattedToday = formatter.format(today)
                        val after1Day = formatter.format(today.plusDays(1))
                        val after7Days = formatter.format(today.plusDays(7))
                        val after30Days = formatter.format(today.plusMonths(1))
                        val after60Days = formatter.format(today.plusMonths(2))

                        fun parseDate(dateString: String): LocalDate? {
                            return try {
                                LocalDate.parse(dateString, formatter)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        }

                        val todayDate = parseDate(formattedToday)
                        val after1DayDate = parseDate(after1Day)
                        val after7DaysDate = parseDate(after7Days)
                        val after30DaysDate = parseDate(after30Days)
                        val after60DaysDate = parseDate(after60Days)

                        if (keyword == "today") { // 오늘까지인 공고 찾기 => 공고 마감일 = 오늘 날짜
                            for (i in wantedList) {
                                val a = i.closeDt
                                // 한글과 공백을 제거하고 순수한 날짜 포맷만 추출
                                val closeDt = a?.replace(Regex("[채용시까지\\s]"), "")
                                if (closeDt != null) {
                                    val closeDtDate = parseDate(closeDt)
                                    if (closeDtDate == todayDate) {
                                        println(i.closeDt)
                                        println(i.company)
                                        println(i.title)
                                        println("______________________________")

                                        val filteredList = wantedList.filter { it.closeDt == keyword }
                                        sharedSelectionViewModel.updateFilteredList(category, filteredList)
                                    }
                                }
                            }
                        } else if (keyword == "tomorrow") { // 내일까지인 공고 찾기 => 공고 마감일 < 오늘 날짜 + 1
                            for (i in wantedList) {
                                val a = i.closeDt
                                // 한글과 공백을 제거하고 순수한 날짜 포맷만 추출
                                val closeDt = a?.replace(Regex("[채용시까지\\s]"), "")
                                if (closeDt != null) {
                                    val closeDtDate = parseDate(closeDt)
                                    if (closeDtDate == after1DayDate) {
                                        println(i.closeDt)
                                        println(i.company)
                                        println(i.title)
                                        println("______________________________")

                                        val filteredList = wantedList.filter { it.closeDt == keyword }
                                        sharedSelectionViewModel.updateFilteredList(category, filteredList)
                                    }
                                }
                            }
                        } else if (keyword == "7days") { // 일주일 이내인 공고 찾기 => 공고 마감일 < 오늘 날짜 + 7
                            for (i in wantedList) {
                                val a = i.closeDt
                                // 한글과 공백을 제거하고 순수한 날짜 포맷만 추출
                                val closeDt = a?.replace(Regex("[채용시까지\\s]"), "")
                                if (closeDt != null) {
                                    val closeDtDate = parseDate(closeDt)
                                    todayDate?.let { today ->
                                        after7DaysDate?.let { after7 ->
                                            if (closeDtDate!! in todayDate..after7) {
                                                println(i.closeDt)
                                                println(i.company)
                                                println(i.title)
                                                println("______________________________")

                                                val filteredList = wantedList.filter { it.closeDt == keyword }
                                                sharedSelectionViewModel.updateFilteredList(category, filteredList)
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (keyword == "30days") { // 한달 이내인 공고 찾기 => 공고 마감일 < 오늘 날짜 + 30
                            for (i in wantedList) {
                                val a = i.closeDt
                                // 한글과 공백을 제거하고 순수한 날짜 포맷만 추출
                                val closeDt = a?.replace(Regex("[채용시까지\\s]"), "")
                                if (closeDt != null) {
                                    val closeDtDate = parseDate(closeDt)
                                    todayDate?.let { today ->
                                        after30DaysDate?.let { after30 ->
                                            if (closeDtDate!! in todayDate..after30) {
                                                println(i.closeDt)
                                                println(i.company)
                                                println(i.title)
                                                println("______________________________")

                                                val filteredList = wantedList.filter { it.closeDt == keyword }
                                                sharedSelectionViewModel.updateFilteredList(category, filteredList)
                                            }
                                        }
                                    }
                                }
                            }
                        } else { // 두달 이내인 공고 찾기 => 공고 마감일 < 오늘 날짜 + 30
                            for (i in wantedList) {
                                val a = i.closeDt
                                // 한글과 공백을 제거하고 순수한 날짜 포맷만 추출
                                val closeDt = a?.replace(Regex("[채용시까지\\s]"), "")
                                if (closeDt != null) {
                                    val closeDtDate = parseDate(closeDt)
                                    todayDate?.let { today ->
                                        after60DaysDate?.let { after60 ->
                                            if (closeDtDate!! in todayDate..after60) {
                                                println(i.closeDt)
                                                println(i.company)
                                                println(i.title)
                                                println("______________________________")

                                                val filteredList = wantedList.filter { it.closeDt == keyword }
                                                sharedSelectionViewModel.updateFilteredList(category, filteredList)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } //if문 closeDt 종료
                } // if 응답이 성공적일때
                else {
                    showErrorToast()
                } //if 응답 실패일때
            } // onResponse 함수 종료

        }) //callback 종료

    } // fetchWantedList 함수 종료

    data class Wanted(
        var wantedAuthNo: String? = null,
        var company: String? = null,
        var title: String? = null,
        var salTpNm: String? = null,
        var sal: String? = null,
        var region: String? = null,
        var holidayTpNm: String? = null,
        var minEdubg: String? = null,
        var career: String? = null,
        var closeDt: String? = null,
        var basicAddr: String? = null,
        var detailAddr: String? = null
    )

    private fun parseXmlResponse(xmlResponse: String?): List<Wanted> {
        val wantedList = mutableListOf<Wanted>()
        val factory = XmlPullParserFactory.newInstance()
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(xmlResponse))

        var eventType = xpp.eventType
        var wantedAuthNo: String? = null
        var company: String? = null
        var title: String? = null
        var salTpNm: String? = null
        var sal: String? = null
        var region: String? = null
        var holidayTpNm: String? = null
        var minEdubg: String? = null
        var career: String? = null
        var closeDt: String? = null
        var basicAddr: String? = null
        var detailAddr: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (xpp.name) {
                        "wantedAuthNo" -> wantedAuthNo = xpp.nextText()
                        "company" -> company = xpp.nextText()
                        "title" -> title = xpp.nextText()
                        "salTpNm" -> salTpNm = xpp.nextText()
                        "sal" -> sal = xpp.nextText()
                        "region" -> region = xpp.nextText()
                        "holidayTpNm" -> holidayTpNm = xpp.nextText()
                        "minEdubg" -> minEdubg = xpp.nextText()
                        "career" -> career = xpp.nextText()
                        "closeDt" -> closeDt = xpp.nextText()
                        "basicAddr" -> basicAddr = xpp.nextText()
                        "detailAddr" -> detailAddr = xpp.nextText()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (xpp.name == "wanted") {
                        wantedList.add(Wanted(wantedAuthNo,company,title,salTpNm,sal, region, holidayTpNm, minEdubg, career, closeDt, basicAddr, detailAddr))
                        wantedAuthNo = null
                        company = null
                        title = null
                        salTpNm = null
                        sal = null
                        region = null
                        holidayTpNm = null
                        minEdubg = null
                        career = null
                        closeDt = null
                        basicAddr = null
                        detailAddr = null
                    }
                }
            }
            eventType = xpp.next()
        } // while문 종료
        return wantedList
    }



    private fun showErrorToast() {
        Toast.makeText(requireContext(), "Failed to fetch wanted list.", Toast.LENGTH_SHORT).show()
    }
}