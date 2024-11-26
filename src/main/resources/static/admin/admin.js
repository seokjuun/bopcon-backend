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
            loadConcerts(); // 콘서트 목록 로드
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

// 수정 폼 표시
function showEditForm(artist) {
    const editSection = document.getElementById('edit-artist-section');
    editSection.style.display = 'block'; // 수정 폼 표시
    document.getElementById('edit-artist-id').value = artist.artistId;
    document.getElementById('edit-mbid').value = artist.mbid;
    document.getElementById('edit-name').value = artist.name;
    document.getElementById('edit-krName').value = artist.krName || '';
    document.getElementById('edit-imgUrl').value = artist.imgUrl || '';
    document.getElementById('edit-snsUrl').value = artist.snsUrl || '';
    document.getElementById('edit-mediaUrl').value = artist.mediaUrl || '';
}


// 수정 취소: 모든 수정 창 숨기기
document.querySelectorAll('.cancel-edit').forEach(button => {
    button.addEventListener('click', () => {
        document.getElementById('edit-artist-section').style.display = 'none';
        document.getElementById('edit-concert-section').style.display = 'none';
    });
});

// 아티스트 수정 요청
document.getElementById('edit-artist-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const artistId = document.getElementById('edit-artist-id').value;
    const formData = {
        mbid: document.getElementById('edit-mbid').value,
        name: document.getElementById('edit-name').value,
        krName: document.getElementById('edit-krName').value,
        imgUrl: document.getElementById('edit-imgUrl').value,
        snsUrl: document.getElementById('edit-snsUrl').value,
        mediaUrl: document.getElementById('edit-mediaUrl').value,
    };

    try {
        const response = await fetch(`/api/admin/artists/${artistId}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(formData),
        });

        if (response.ok) {
            alert('Artist updated successfully!');
            document.getElementById('edit-artist-section').style.display = 'none';
            loadArtists(); // Reload artist list
        } else {
            const error = await response.json();
            alert(`Error: ${error.message}`);
        }
    } catch (err) {
        alert('An error occurred while updating artist.');
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
                    <td>${artist.imgUrl || ''}</td>
                    <td>${artist.snsUrl || ''}</td>
                    <td>${artist.mediaUrl || ''}</td>
                    <td>
                        <button onclick='showEditForm(${JSON.stringify(artist)})'>Edit</button>
                        <button onclick='deleteArtist(${artist.artistId})'>Delete</button>
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

// 콘서트 등록 처리
document.getElementById('new-concert-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = {
        artistId: parseInt(document.getElementById('artist-id').value), // 숫자로 전달
        title: document.getElementById('title').value,
        subTitle: document.getElementById('sub-title').value || null,
        date: document.getElementById('date').value,
        venueName: document.getElementById('venue-name').value,
        cityName: document.getElementById('city-name').value,
        countryName: document.getElementById('country-name').value,
        countryCode: document.getElementById('country-code').value,
        ticketPlatforms: document.getElementById('ticket-platforms').value || null,
        ticketUrl: document.getElementById('ticket-url').value || null,
        posterUrl: document.getElementById('poster-url').value || null,
        genre: document.getElementById('genre').value || null,
        concertStatus: document.getElementById('concert-status').value,
    };
    await fetchConcertAPI('/api/admin/new-concert', 'POST', formData);
});

// 콘서트 수정 폼 표시
function showEditConcertForm(concert) {
    const editForm = document.getElementById('edit-concert-section');
    editForm.style.display = 'block';
    document.getElementById('edit-concert-id').value = concert.newConcertId;
    document.getElementById('edit-concert-artistId').value = concert.artistId;
    document.getElementById('edit-title').value = concert.title;
    document.getElementById('edit-sub-title').value = concert.subTitle || '';
    document.getElementById('edit-date').value = concert.date;
    document.getElementById('edit-venue-name').value = concert.venueName;
    document.getElementById('edit-city-name').value = concert.cityName;
    document.getElementById('edit-country-name').value = concert.countryName;
    document.getElementById('edit-country-code').value = concert.countryCode;
    document.getElementById('edit-ticket-platforms').value = concert.ticketPlatforms || '';
    document.getElementById('edit-ticket-url').value = concert.ticketUrl || '';
    document.getElementById('edit-poster-url').value = concert.posterUrl || '';
    document.getElementById('edit-genre').value = concert.genre || '';
    document.getElementById('edit-concert-status').value = concert.concertStatus;
}

// 콘서트 수정 처리
document.getElementById('edit-concert-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const concertId = document.getElementById('edit-concert-id').value;
    const formData = {
        artistId: document.getElementById('edit-concert-artistId').value,
        title: document.getElementById('edit-title').value,
        subTitle: document.getElementById('edit-sub-title').value || null,
        date: document.getElementById('edit-date').value,
        venueName: document.getElementById('edit-venue-name').value,
        cityName: document.getElementById('edit-city-name').value,
        countryName: document.getElementById('edit-country-name').value,
        countryCode: document.getElementById('edit-country-code').value,
        ticketPlatforms: document.getElementById('edit-ticket-platforms').value || null,
        ticketUrl: document.getElementById('edit-ticket-url').value || null,
        posterUrl: document.getElementById('edit-poster-url').value || null,
        genre: document.getElementById('edit-genre').value || null,
        concertStatus: document.getElementById('edit-concert-status').value,
    };
    await fetchConcertAPI(`/api/admin/new-concert/${concertId}`, 'PUT', formData);
});

// 콘서트 삭제
async function deleteConcert(concertId) {
    await fetchConcertAPI(`/api/admin/new-concerts/${concertId}`, 'DELETE');
}

// 콘서트 목록 로드
async function loadConcerts() {
    const response = await fetch('/api/new-concerts', { headers: getHeaders() });
    const concerts = await response.json();
    const tbody = document.getElementById('concert-table').querySelector('tbody');
    tbody.innerHTML = ''; // Clear existing rows
    concerts.forEach((concert) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${concert.newConcertId}</td>
            <td>${concert.artistId}</td>
            <td>${concert.title}</td>
            <td>${concert.date}</td>
            <td>${concert.venueName}</td>
            <td>
                <button onclick='showEditConcertForm(${JSON.stringify(concert)})'>Edit</button>
                <button onclick='deleteConcert(${concert.newConcertId})'>Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// API 호출 공통 함수
async function fetchConcertAPI(url, method, data) {
    const response = await fetch(url, {
        method,
        headers: getHeaders(),
        body: data ? JSON.stringify(data) : null,
    });
    if (response.ok) {
        alert('Operation successful!');
        loadConcerts(); // Reload concert list
    } else {
        alert('Operation failed!');
    }
}

