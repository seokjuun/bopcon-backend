let accessToken = ""; // 로그인 후 발급받은 액세스 토큰 저장

// 로그인 처리
document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const loginData = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            const data = await response.json();
            accessToken = data.accessToken; // 액세스 토큰 저장
            alert('Login successful!');
            document.getElementById('login-section').style.display = 'none'; // 로그인 섹션 숨기기
            document.getElementById('admin-section').style.display = 'block'; // 관리자 섹션 보이기
            loadArtists(); // 아티스트 목록 로드
        } else {
            const error = await response.json();
            alert(`Login failed: ${error.message}`);
        }
    } catch (err) {
        alert('An error occurred during login.');
        console.error(err);
    }
});

// 헤더 설정 함수
function getHeaders() {
    return {
        "Authorization": `Bearer ${accessToken}`,
        "Content-Type": "application/json"
    };
}

// 아티스트 등록
document.getElementById('artist-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = {
        mbid: document.getElementById('mbid').value,
        name: document.getElementById('name').value,
        krName: document.getElementById('krName').value,
        imgUrl: document.getElementById('imgUrl').value,
        snsUrl: document.getElementById('snsUrl').value,
        mediaUrl: document.getElementById('mediaUrl').value,
    };

    try {
        const response = await fetch('/api/admin/artist', {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(formData),
        });

        if (response.ok) {
            alert('Artist added successfully!');
            loadArtists();
        } else {
            const error = await response.json();
            alert(`Error: ${error.message}`);
        }
    } catch (err) {
        alert('An error occurred while adding artist.');
        console.error(err);
    }
});

// 아티스트 목록 로드
async function loadArtists() {
    try {
        const response = await fetch('/api/artists', { headers: getHeaders() });
        if (response.ok) {
            const artists = await response.json();
            const tbody = document.getElementById('artist-table').querySelector('tbody');
            tbody.innerHTML = ''; // Clear existing rows

            artists.forEach(artist => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${artist.artistId}</td>
                    <td>${artist.name}</td>
                    <td>${artist.krName || ''}</td>
                    <td>${artist.mbid}</td>
                    <td>
                        <button onclick="deleteArtist(${artist.artistId})">Delete</button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        } else {
            alert('Failed to load artists.');
        }
    } catch (err) {
        alert('An error occurred while loading artists.');
        console.error(err);
    }
}

// 아티스트 삭제
async function deleteArtist(artistId) {
    try {
        const response = await fetch(`/api/artists/${artistId}`, {
            method: 'DELETE',
            headers: getHeaders(),
        });

        if (response.ok) {
            alert('Artist deleted successfully!');
            loadArtists();
        } else {
            alert('Failed to delete artist.');
        }
    } catch (err) {
        alert('An error occurred while deleting artist.');
        console.error(err);
    }
}
