function selectMBTI(mbti, event) {
    event.preventDefault(); // 기본 링크 동작을 막음
    event.preventDefault(); // 폼의 기본 제출 동작을 막음
    document.getElementById("mbtiInput").value = mbti;
    document.getElementById("mbtiButton").innerText = mbti + " ▼";
}

document.getElementById('registerForm').addEventListener('submit', function(event) {
    event.preventDefault(); // 폼의 기본 제출 동작을 막음

    var password1 = document.getElementById('mpw').value;
    var password2 = document.getElementById('mpwc').value;
    var errorMessage = document.getElementById('errorMessage');

    // 에러 메시지 초기화
    errorMessage.textContent = '';

    // 비밀번호 일치 확인
    if (password1 !== password2) {
        errorMessage.textContent = "비밀번호가 서로 다릅니다.";
        alert('비밀번호가 서로 다릅니다.');
        return;
    }

    // 비밀번호가 문자와 숫자의 조합인지 확인
    var passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
    if (!passwordPattern.test(password1)) {
        errorMessage.textContent = '비밀번호는 8장 이상의 문자와 숫자의 조합이 돼야합니다.';
        alert('비밀번호는 8장 이상의 문자와 숫자의 조합이 돼야합니다.');
        return;
    }

    // 모든 검증을 통과한 경우 폼을 제출하거나 다음 작업 수행
    this.submit(); // 실제 폼 제출
});