package com.mrsarayra.agileengine.services;

import com.google.gson.Gson;
import com.mrsarayra.agileengine.classes.Picture;
import com.mrsarayra.agileengine.classes.PictureDetails;
import com.mrsarayra.agileengine.classes.RequestToken;
import com.mrsarayra.agileengine.classes.ResponseImages;
import com.mrsarayra.agileengine.classes.ResponseToken;
import com.mrsarayra.agileengine.dao.PhotoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class InitializationService {

    private static final String apiKey = "23567b218376f79d9415"; // TODO: add to app properties

    private RestTemplate restTemplate = new RestTemplate();
    private Gson gson = new Gson();

    @Autowired
    private PhotoService photoService;


    @PostConstruct
    public void init() {
        // Auth
        String token = auth(); // TODO: assert

        // Get images
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer 937c05b933050b8d38cf320684684284ca182304");

        boolean hasMore = true;

        for (int page = 1; hasMore; page++) {
            ResponseEntity<String> res = sendHttpRequest("http://interview.agileengine.com/images?page=" + page, token, HttpMethod.GET, null, httpHeaders, String.class);
            ResponseImages images = gson.fromJson(res.getBody(), ResponseImages.class); // introduce map

            // Cache
            List<Picture> picturesList = images.getPictures();
            picturesList.forEach(picture -> {
                PhotoEntity photoEntity = new PhotoEntity();
                String pictureId = picture.getId();
                photoEntity.setOriginalId(pictureId);
                photoEntity.setCroppedPicture(picture.getCropped_picture());

                // Get photo details
                ResponseEntity<String> picDetailsRes = sendHttpRequest("http://interview.agileengine.com/images/" + pictureId, token, HttpMethod.GET, null, httpHeaders, String.class);
                PictureDetails pictureDetails = gson.fromJson(picDetailsRes.getBody(), PictureDetails.class);
                photoEntity.setAuthor(pictureDetails.getAuthor());
                photoEntity.setCamera(pictureDetails.getCamera());
                photoEntity.setFullPicture(pictureDetails.getFull_picture());
                HashSet<String> tags = new HashSet<>(Arrays.asList(pictureDetails.getTags().split(" ")));
                photoEntity.setTags(tags);

                photoService.create(photoEntity);
            });
            hasMore = images.isHasMore();
        }
    }


    private String auth() {
        RequestToken requestToken = new RequestToken(apiKey);
        String requestJson = gson.toJson(requestToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(requestJson, httpHeaders);
        String res = restTemplate.postForObject("http://interview.agileengine.com/auth", request, String.class);
        ResponseToken response = gson.fromJson(res, ResponseToken.class);
        return response.getToken();
    }


    public static <T, R> ResponseEntity<R> sendHttpRequest(String url, String token, HttpMethod method, T body, HttpHeaders headers, Class<R> returnType) {
        // asserts
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(url, method, new HttpEntity<>(body, headers), returnType);
    }


}
