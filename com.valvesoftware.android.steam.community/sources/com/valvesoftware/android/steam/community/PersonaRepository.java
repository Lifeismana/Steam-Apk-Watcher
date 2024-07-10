package com.valvesoftware.android.steam.community;

import com.valvesoftware.android.steam.community.jsontranslators.PersonaTranslator;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class PersonaRepository {
    public static void getDetailedPersonaInfo(Collection<String> collection, final RepositoryCallback<List<Persona>> repositoryCallback) {
        List<RequestBuilder> userSummariesRequestBuilder = Endpoints.getUserSummariesRequestBuilder(collection);
        final AtomicInteger atomicInteger = new AtomicInteger(userSummariesRequestBuilder.size());
        for (RequestBuilder requestBuilder : userSummariesRequestBuilder) {
            requestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.PersonaRepository.1
                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onSuccess(JSONObject jSONObject) {
                    List<Persona> translateList = PersonaTranslator.translateList(jSONObject);
                    int decrementAndGet = atomicInteger.decrementAndGet();
                    RepositoryCallback repositoryCallback2 = repositoryCallback;
                    if (repositoryCallback2 != null) {
                        repositoryCallback2.dataAvailable(translateList);
                        if (decrementAndGet == 0) {
                            repositoryCallback.end();
                        }
                    }
                }

                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onError(RequestErrorInfo requestErrorInfo) {
                    RepositoryCallback repositoryCallback2;
                    if (atomicInteger.decrementAndGet() != 0 || (repositoryCallback2 = repositoryCallback) == null) {
                        return;
                    }
                    repositoryCallback2.end();
                }
            });
            sendRequest(requestBuilder);
        }
    }

    public static void getDetailedPersonaInfo(String str, final RepositoryCallback<Persona> repositoryCallback) {
        getDetailedPersonaInfo(Collections.singletonList(str), new RepositoryCallback<List<Persona>>() { // from class: com.valvesoftware.android.steam.community.PersonaRepository.2
            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void dataAvailable(List<Persona> list) {
                if (RepositoryCallback.this == null || list == null || list.size() <= 0) {
                    return;
                }
                RepositoryCallback.this.dataAvailable(list.get(0));
            }

            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void end() {
                RepositoryCallback repositoryCallback2 = RepositoryCallback.this;
                if (repositoryCallback2 != null) {
                    repositoryCallback2.end();
                }
            }
        });
    }

    private static void sendRequest(RequestBuilder requestBuilder) {
        SteamCommunityApplication.GetInstance().sendRequest(requestBuilder);
    }
}
