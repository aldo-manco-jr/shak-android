package org.aldofrank.shak.services;

import retrofit2.http.GET;

public interface PeopleService {

    /*GetAllUsers(): Observable<any>{
        return this.http.get(BASE_URL + '/users');
    }

    GetUserById(id): Observable<any>{
        return this.http.get(`${BASE_URL}/user/${id}`);
    }

    GetUserByName(username): Observable<any>{
        return this.http.get(`${BASE_URL}/username/${username}`);
    }*/

    @GET("users")
}
