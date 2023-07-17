import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a23_hf069.HomeNotificationFragment
import com.example.a23_hf069.HomeSearchFragment
import com.example.a23_hf069.ListAdapterGrid
import com.example.a23_hf069.R

class HomeFragment : Fragment() {

    private lateinit var notificationButton: ImageButton
    private lateinit var userId: String // 사용자 아이디

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바에서 notificationButton을 찾아 클릭 리스너를 설정
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        notificationButton = toolbar.findViewById(R.id.notificationButton)
        notificationButton.setOnClickListener {
            // notificationButton을 클릭하면 알림 화면 HomeNotificationFragment로 전환
            val notificationFragment = HomeNotificationFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, notificationFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // 사용자 아이디 받아오기
        if (arguments != null) {
            userId = arguments?.getString("userId", "") ?: ""
        }

        val textViewUserName = rootView.findViewById<TextView>(R.id.textViewUserName)
        textViewUserName.text = userId
        val textViewUserName2 = rootView.findViewById<TextView>(R.id.textViewUserName2)
        textViewUserName2.text = userId

        // RecyclerView 관련 코드 추가

        // RecyclerView에 표시할 아이템의 데이터 목록 / 임시 데이터로 8개 생성함.
        val list = arrayListOf("Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8")

        // GridLayoutManager를 생성하여 RecyclerView의 레이아웃 매니저로 설정
        val listManager = GridLayoutManager(requireContext(), 2) // 2열 그리트 형태로 아이템 표시
        // ListAdapterGrid 객체를 생성하여 RecyclerView의 어댑터로 설정
        val listAdapter = ListAdapterGrid(list)

        val recyclerList = rootView.findViewById<RecyclerView>(R.id.recyclerGridView)
        recyclerList.apply {
            setHasFixedSize(true) // 아이템 크기 고정
            isNestedScrollingEnabled = false // 스크롤 비활성화
            layoutManager = listManager // RecyclerView의 레이아웃 매니저 설정
            adapter = listAdapter // RecyclerView의 어댑터 설정
        }

        // EditText을 클릭하면 검색 화면 searchFragment로 전환
        val searchContent = rootView.findViewById<EditText>(R.id.searchContent) // EditText를 rootView에서 찾아옴
        searchContent.setOnFocusChangeListener { _, hasFocus -> // EditText의 포커스 변화를 감지하는 리스너를 설정
            if (hasFocus) {
                val searchFragment = HomeSearchFragment()
                requireActivity().supportFragmentManager.beginTransaction() // 프래그먼트 간 전환 수행
                    .replace(R.id.fl_container, searchFragment) // 프래그먼트 교체
                    .addToBackStack(null) // 이전 프래그먼트로 돌아가기
                    .commit()
                hideKeyboard() // 키보드 숨김 처리
            }
        }

        return rootView
    }

    private fun hideKeyboard() {
        // 키보드 숨김 처리를 수행
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        view?.clearFocus()
    }
}
