package com.mrsarayra.agileengine.services;

import com.mrsarayra.agileengine.classes.Picture;
import com.mrsarayra.agileengine.classes.RequestToken;
import com.mrsarayra.agileengine.classes.ResponseImages;
import com.mrsarayra.agileengine.classes.ResponsePictureDetails;
import com.mrsarayra.agileengine.classes.ResponseToken;
import com.mrsarayra.agileengine.dao.PhotoEntity;
import com.mrsarayra.agileengine.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpMethod.POST;

@Slf4j
@Service
public class InitializationService {

    @Value("${app.cache.refresh.rate:60}")
    private long REFRESH_CACHE_RATE;

    @Value("${app.api.key}")
    private String API_KEY;

    private String token;

    @Autowired
    private PhotoService photoService;


    @PostConstruct
    public void init() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        executorService.scheduleAtFixedRate(() -> {
            token = auth();
            fetchImages();
        }, 5, REFRESH_CACHE_RATE, TimeUnit.SECONDS);
    }


    private void fetchImages() {
        log.info("Fetching images from API...");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

        boolean hasMore = true;
        for (int page = 1; hasMore; page++) {
            ResponseEntity<String> res = sendHttpRequest("http://interview.agileengine.com/images?page=" + page, HttpMethod.GET, null, httpHeaders, String.class);
            ResponseImages responseImages = JsonUtils.jsonToObj(res.getBody(), ResponseImages.class);
            Assert.notNull(responseImages, "Failed to get pictures");

            // Caching to DB
            List<Picture> picturesList = responseImages.getPictures();
            picturesList.forEach(picture -> {
                PhotoEntity photoEntity = new PhotoEntity();
                String pictureId = picture.getId();
                photoEntity.setOriginalId(pictureId);
                photoEntity.setCroppedPicture(picture.getCropped_picture());

                // Get photo details
                ResponseEntity<String> picDetailsRes = sendHttpRequest("http://interview.agileengine.com/images/" + pictureId, HttpMethod.GET, null, httpHeaders, String.class);
                ResponsePictureDetails pictureDetailsResponse = JsonUtils.jsonToObj(picDetailsRes.getBody(), ResponsePictureDetails.class);
                Assert.notNull(pictureDetailsResponse, "Failed to get picture details");

                photoEntity.setAuthor(pictureDetailsResponse.getAuthor());
                photoEntity.setCamera(pictureDetailsResponse.getCamera());
                photoEntity.setFullPicture(pictureDetailsResponse.getFull_picture());
                String tagsStr = pictureDetailsResponse.getTags();
                if (tagsStr != null && tagsStr.length() > 0) {
                    HashSet<String> tags = new HashSet<>(Arrays.asList(tagsStr.split(" ")));
                    photoEntity.setTags(tags);
                }

                // Saving to DB
                try {
                    photoService.create(photoEntity);
                } catch (Exception e) {
                    log.error("Failed to create entity due to {}", e.getMessage());
                }
            });
            hasMore = responseImages.isHasMore();
        }
        log.info("Images cached successfully...");
    }


    private String auth() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RequestToken requestToken = new RequestToken(API_KEY);
        ResponseEntity<String> responseEntity = sendHttpRequest("http://interview.agileengine.com/auth", POST,
                requestToken, httpHeaders, String.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Failed to Authorize!");
        }

        ResponseToken responseToken = JsonUtils.jsonToObj(responseEntity.getBody(), ResponseToken.class);
        Assert.notNull(requestToken, "Failed to get token");
        String token = responseToken.getToken();
        Assert.hasText(token, "Invalid token: " + token);

        return token;
    }


    public static <T, R> ResponseEntity<R> sendHttpRequest(String url, HttpMethod method, T body, HttpHeaders headers, Class<R> returnType) {
        Assert.hasText(url, "URL cannot be blank");
        Assert.notNull(returnType, "Return type cannot be blank");
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(url, method, new HttpEntity<>(body, headers), returnType);
    }

}
