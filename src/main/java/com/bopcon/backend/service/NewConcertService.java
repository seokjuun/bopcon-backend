package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.dto.UpdateNewConcertRequest;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.NewConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequiredArgsConstructor
@Service
public class NewConcertService {
    private final NewConcertRepository newConcertRepository;
    private final ArtistRepository artistRepository;
    private final S3Service s3Service;

    // 뉴 콘서트 추가 메서드
    @CacheEvict(value = {"allNewConcerts", "newConcertsByGenre"}, allEntries = true)
    public NewConcert save(AddNewConcertRequest request, MultipartFile file) {
        String posterUrl = null;
        if (file != null && !file.isEmpty()) {
            posterUrl = s3Service.upload(file, "concert-posters");
            // 새로운 request 객체를 생성해 posterUrl를 주입
            request = new AddNewConcertRequest(
                    request.getArtistId(),
                    request.getTitle(),
                    request.getSubTitle(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getVenueName(),
                    request.getCityName(),
                    request.getCountryName(),
                    request.getCountryCode(),
                    request.getTicketPlatforms(),
                    request.getTicketUrl(),
                    posterUrl, // 여기서 posterUrl 반영
                    request.getGenre(),
                    request.getConcertStatus()
            );
        }

        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));
        return newConcertRepository.save(request.toNewConcert(artist));
    }

    // 뉴 콘서트 수정
    // 뉴 콘서트 수정 (파일 추가)
    @Transactional
    @CacheEvict(value = {"allNewConcerts", "newConcertsByGenre"}, allEntries = true)
    public NewConcert update(long newConcertId, UpdateNewConcertRequest request, MultipartFile file) {
        NewConcert newConcert = newConcertRepository.findById(newConcertId)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + newConcertId));

        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));

        String updatedPosterUrl = request.getPosterUrl();
        if (file != null && !file.isEmpty()) {
            updatedPosterUrl = s3Service.upload(file, "concert-posters");
        }

        UpdateNewConcertRequest updatedRequest = new UpdateNewConcertRequest(
                request.getArtistId(),
                request.getTitle(),
                request.getSubTitle(),
                request.getStartDate(),
                request.getEndDate(),
                request.getVenueName(),
                request.getCityName(),
                request.getCountryName(),
                request.getCountryCode(),
                request.getTicketPlatforms(),
                request.getTicketUrl(),
                updatedPosterUrl, // 수정된 posterUrl 반영
                request.getGenre(),
                request.getConcertStatus()
        );

        newConcert.updateNewConcert(updatedRequest, artist);
        return newConcert;
    }

    // 콘서트 목록 가져오기
    @Cacheable(value = "allNewConcerts", key = "'allConcerts'")
    public List<NewConcert> findAllNewConcerts(){ return newConcertRepository.findAll(); }

    // 콘서트 (장르 필터) 목록 가져오기
    @Cacheable(value = "newConcertsByGenre", key = "#genre")
    public List<NewConcert> findNewConcertsByGenre(String genre){
        return newConcertRepository.findByGenre(genre);
    }

    // 콘서트 조회
    @Cacheable(value = "singleConcert", key = "#concertId")
    public NewConcert findByConcertId(long concertId){
        return newConcertRepository.findById(concertId)
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ concertId));
    }

    // 콘서트 삭제
    @CacheEvict(value = {"allNewConcerts", "newConcertsByGenre", "singleConcert"}, allEntries = true)
    public void delete(long concertId){
        newConcertRepository.deleteById(concertId);
    }

    // 아티스트 콘서트 가져오기
    @Cacheable(value = "newConcertsByArtist", key = "#artistId")
    public List<NewConcert> findNewConcertsByArtistId(Long artistId){
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()->new IllegalArgumentException("Invalid artistId: "+ artistId));

        return newConcertRepository.findByArtist(artist);
    }
}
