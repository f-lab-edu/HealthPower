/*function mypage(){
    fetch("/members/mypage",{
    method: "GET",
    headers:{"Content-Type":"application/json"},
    credentials:"include"
   })
   .then(res => {
   })

}*/

function logout() {
  fetch("/members/logout2", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include"
  })
  .then(res => {
    if (res.redirected) {
      // ✅ Spring의 redirect:/members/login 처리된 경우
      window.location.href = res.url;
    } else if (res.ok) {
      window.location.href = "/members/login2";
    } else {
      alert("로그아웃 실패!");
    }
  })
  .catch(err => {
    console.error("에러:", err);
    alert("서버 오류로 로그아웃에 실패했습니다.");
  });
}

function login(){
    window.location.href = "/members/login2";
}

function secureNavigate(url) {
  fetch(url, {
    method: 'GET',
    credentials: 'include'
  })
  .then(res => {
    if (res.status === 401) {
      alert('로그인이 필요한 기능입니다.');
    } else if (res.ok) {
      // 요청 성공 시 실제 페이지 이동
      window.location.href = url;
    } else {
      alert('서버 오류가 발생했습니다.');
    }
  })
  .catch(err => {
    console.error('요청 중 오류:', err);
    alert('서버와의 통신에 실패했습니다.');
  });
}