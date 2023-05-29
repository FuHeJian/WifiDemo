package test;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * test
 */
public interface NetInterface {

    @GET("api/v3/exchangeInfo")
    @Headers({
            "clienttype: android",
            "x-trace-id: android_d1e8cf57-72aa-4b26-a4c9-f90cf0bb865d",
            "fvideo-id: 23ecaa9d38d9a1d0ab751e64e36719960b8c2df4",
            "lang: zh-CN",
            "versioncode: 26409",
            "versionname: 2.64.9",
            "isnight: false",
            "bnc-app-mode: pro",
            "bnc-time-zone: Asia/Shanghai",
            "device-info: eyJkZXZpY2VfaWQiOiIiLCJhX2Jvb3Rsb2FkZXIiOiJ1bmtub3duIiwiYV9icmFuZCI6InNhbXN1bmciLCJhX2xvY2F0aW9uX2NpdHkiOiJ1bmtub3duIiwiYV9jcHVfYWJpIjoiW3g4Nl82NCwgeDg2LCBhcm02NC12OGEsIGFybWVhYmktdjdhLCBhcm1lYWJpXSIsImFfZGV2aWNlX2xvZ2luX25hbWUiOiJncmFjZWx0ZSIsImRldmljZV9uYW1lIjoiU00tRzk3MzAiLCJhX2Rpc3BsYXkiOiJQUTNBLjE5MDcwNS4wMDMgcmVsZWFzZS1rZXlzIiwiYV9maW5nZXJwcmludCI6IkFuZHJvaWQvZ3JhY2VsdGV4eC9ncmFjZWx0ZTo5L1BRM0EuMTkwNzA1LjAwMy9HOTcwMEZYWFUxQVBGTzp1c2VyL3JlbGVhc2Uta2V5cyIsImFfaG9zdCI6InVidW50dSIsImFfZGV2aWNlX3ZlcnNpb25faWQiOiJQUTNBLjE5MDcwNS4wMDMiLCJhX2ltZWkiOiJ1bmtub3duIiwiYV9yb21fc2l6ZSI6IjIwLDg0OE1CIiwiYV9nZXRfbGluZV9udW1iZXIiOiJ1bmtub3duIiwiYV9wcm9kdWN0IjoiU00tRzk3MzAiLCJhX3JhbV9zaXplICI6IjcsOTcyTUIiLCJhX3NjcmVlbkhlaWdodCI6Ijk2MCIsImFfc2NyZWVuV2lkdGgiOiI1NDAiLCJhX3NkayI6IjI4IiwiYV9zZXJpYWxfaW5mbyI6InVua25vd24iLCJhX3NpbV9zZXJpYWxfbnVtYmVyIjoidW5rbm93biIsImFfYnVpbGRfdGltZSI6IjE2NTg4OTc5MDcwMDAiLCJhX3VzZXIiOiJidWlsZCIsImJyYW5kX21vZGVsIjoic2Ftc3VuZ1NNLUc5NzMwIiwiYV9hcHBfaW5zdGFsbF9kYXRlIjoiMTY4NDUxNTE2Nzc3NSIsImp1ZGdlX3Jvb3QiOjAsInNjcmVlbl9yZXNvbHV0aW9uIjoiNTQwKjk2MCIsInN5c3RlbV9sYW5nIjoiemgtQ04iLCJzeXN0ZW1fdmVyc2lvbiI6IjI4IiwidGltZXpvbmUiOiJHTVQrMDgwMCJ9",
            "bnc-app-channel: binance",
            "bnc-uuid: 1f42dbb2c7093d2c8232abe741acfcad",
            "mclient-x-tag: pch5D9lsORjgObhyjdSK",
            "bnc-currency: CNY",
            "referer: https://www.binance.com/",
            "cache-control: no-cache, no-store",
            "x-seccheck-sig: a1.3.0#KgAAAA4AAABdAAAAFAAAAOOLACjoga-IYA4KV7h_DlOhDUcSFF2LNBkqSqUZl6u2RpeF7GhoXZYYgypMt5t9dnoC8MuQKc__jGOsG4zsP-UGLP6Z8uu6dtOrVukTWM1Z0y7qpv7I959d2cP-d1243zefRsc",
            "x-seccheck-token: a1.3.0#-gIAAI4BAACNAAAAdAAAAGJ0VujH9QR1j9Kzbjv0HYpkrRdWx2-XNRnAQu8CLX9m_YEkW8TPgpa6JehhFIzdo9_Dnkd2OyHm7cYQX31b81ChE_L2iECEt66dWbgsrAFDye0befJA6v7y4g3f2DxS_ojqS0KKjqEMIa-CEMXT9PCnGadrdDhpNOvLVPoIilZuLolT9Lm2Jh4gbUMKvI3ufRBMk82n96Z0c9wqPwLP9YQYXNj2D-ir-ZQuEQHuJXF9O2Or5hophrqhGNdBCa9NKNZqxi8z9BxylPSjsCMh4ROZQ_5cIEYw921S5awaEBF893XwPXILXrzXdN4K5RUgVj_hYo8pbxrHZ64qtP0_hp4G_4tK2b_ktE-2MX6i5RpD4Rh5e8W04dGpuEgnqhul7MaJye8SzYRF2yNMb1IxapkThZb5znbMf5s6lhCDPrzLbPDeyJQiUgs1UCrHZGUamw1e8R95S_ti4ELKA79pbZaKbZmuSUrLlL4Tmb6XorSa4kTztAg9J2Z8dUM_9qatDsO6xB5UhYGXgLc0KSNIjia3X0ucx5HppEimU8RBJJGrruDoy1fApkFuu7kwv-CAL7hfIQwPHENTpGbVz-hetDBcs7UILFuRDLbFzYPDe09iFlJ0NVcQOcLejSfgckibTQAld5jwg3Fzk_zfmoeQOFMWISQOnT3dFD6YRVMI3hnaFW19ve_kuUnMSmm2Ne9i9BHzqexct3WczLffMnBLcJkXd2LW3vnPatlKs7BffX0EmCfRMhgi8c-mhNMbNm23Hq4jM4q6ZHgghxeWQl0Pz3CDoACCoHOrtZ6RhnZX7BPtI5EzWd5F6BGTxLOrdpwh0YmGHO6VMehSwuTqRAffciAc5ZJn7Ism7CXjqUEuexeNS9odBSqv3BS-uGmLmDdqQNlDBj8tFJpu2AWRszoT2CiEEp8GCMav5AxpDelVzY5fvOEkQBg0vJn3O10JGujpB7iVMpzg-C8PWcfzSPQQw32eLGCZSeXZfJrim1vETntMClRRF7dJQRjUGfqQTMWa5jbVR8c_RGH-SWLpEsaM_EOB2f-GL0yFm7zisfu0vCBfVVJ37gLK-IB3JxPIQwSRSg0V-DRlhCzccmov8hn1Uf5v1w0HykNFPN7U6RhA9Jp67yrqwLoVNW_aYIquxVRzO-WmoeMsGkex72BaStTgEDuFifKQKcnbHLx5gT2LE1UN9Jr69NXBZ_0ZdkDyAnjYhYdj56c#5EEB5C53",
            "user-agent: okhttp/4.10.0"
    })
    Call<ResponseBody> getSymbols();

    @GET
    @Headers({
            "clienttype: android",
            "x-trace-id: android_d1e8cf57-72aa-4b26-a4c9-f90cf0bb865d",
            "fvideo-id: 23ecaa9d38d9a1d0ab751e64e36719960b8c2df4",
            "lang: zh-CN",
            "versioncode: 26409",
            "versionname: 2.64.9",
            "isnight: false",
            "bnc-app-mode: pro",
            "bnc-time-zone: Asia/Shanghai",
            "device-info: eyJkZXZpY2VfaWQiOiIiLCJhX2Jvb3Rsb2FkZXIiOiJ1bmtub3duIiwiYV9icmFuZCI6InNhbXN1bmciLCJhX2xvY2F0aW9uX2NpdHkiOiJ1bmtub3duIiwiYV9jcHVfYWJpIjoiW3g4Nl82NCwgeDg2LCBhcm02NC12OGEsIGFybWVhYmktdjdhLCBhcm1lYWJpXSIsImFfZGV2aWNlX2xvZ2luX25hbWUiOiJncmFjZWx0ZSIsImRldmljZV9uYW1lIjoiU00tRzk3MzAiLCJhX2Rpc3BsYXkiOiJQUTNBLjE5MDcwNS4wMDMgcmVsZWFzZS1rZXlzIiwiYV9maW5nZXJwcmludCI6IkFuZHJvaWQvZ3JhY2VsdGV4eC9ncmFjZWx0ZTo5L1BRM0EuMTkwNzA1LjAwMy9HOTcwMEZYWFUxQVBGTzp1c2VyL3JlbGVhc2Uta2V5cyIsImFfaG9zdCI6InVidW50dSIsImFfZGV2aWNlX3ZlcnNpb25faWQiOiJQUTNBLjE5MDcwNS4wMDMiLCJhX2ltZWkiOiJ1bmtub3duIiwiYV9yb21fc2l6ZSI6IjIwLDg0OE1CIiwiYV9nZXRfbGluZV9udW1iZXIiOiJ1bmtub3duIiwiYV9wcm9kdWN0IjoiU00tRzk3MzAiLCJhX3JhbV9zaXplICI6IjcsOTcyTUIiLCJhX3NjcmVlbkhlaWdodCI6Ijk2MCIsImFfc2NyZWVuV2lkdGgiOiI1NDAiLCJhX3NkayI6IjI4IiwiYV9zZXJpYWxfaW5mbyI6InVua25vd24iLCJhX3NpbV9zZXJpYWxfbnVtYmVyIjoidW5rbm93biIsImFfYnVpbGRfdGltZSI6IjE2NTg4OTc5MDcwMDAiLCJhX3VzZXIiOiJidWlsZCIsImJyYW5kX21vZGVsIjoic2Ftc3VuZ1NNLUc5NzMwIiwiYV9hcHBfaW5zdGFsbF9kYXRlIjoiMTY4NDUxNTE2Nzc3NSIsImp1ZGdlX3Jvb3QiOjAsInNjcmVlbl9yZXNvbHV0aW9uIjoiNTQwKjk2MCIsInN5c3RlbV9sYW5nIjoiemgtQ04iLCJzeXN0ZW1fdmVyc2lvbiI6IjI4IiwidGltZXpvbmUiOiJHTVQrMDgwMCJ9",
            "bnc-app-channel: binance",
            "bnc-uuid: 1f42dbb2c7093d2c8232abe741acfcad",
            "mclient-x-tag: pch5D9lsORjgObhyjdSK",
            "bnc-currency: CNY",
            "referer: https://www.binance.com/",
            "cache-control: no-cache, no-store",
            "x-seccheck-sig: a1.3.0#KgAAAA4AAABdAAAAFAAAAOOLACjoga-IYA4KV7h_DlOhDUcSFF2LNBkqSqUZl6u2RpeF7GhoXZYYgypMt5t9dnoC8MuQKc__jGOsG4zsP-UGLP6Z8uu6dtOrVukTWM1Z0y7qpv7I959d2cP-d1243zefRsc",
            "x-seccheck-token: a1.3.0#-gIAAI4BAACNAAAAdAAAAGJ0VujH9QR1j9Kzbjv0HYpkrRdWx2-XNRnAQu8CLX9m_YEkW8TPgpa6JehhFIzdo9_Dnkd2OyHm7cYQX31b81ChE_L2iECEt66dWbgsrAFDye0befJA6v7y4g3f2DxS_ojqS0KKjqEMIa-CEMXT9PCnGadrdDhpNOvLVPoIilZuLolT9Lm2Jh4gbUMKvI3ufRBMk82n96Z0c9wqPwLP9YQYXNj2D-ir-ZQuEQHuJXF9O2Or5hophrqhGNdBCa9NKNZqxi8z9BxylPSjsCMh4ROZQ_5cIEYw921S5awaEBF893XwPXILXrzXdN4K5RUgVj_hYo8pbxrHZ64qtP0_hp4G_4tK2b_ktE-2MX6i5RpD4Rh5e8W04dGpuEgnqhul7MaJye8SzYRF2yNMb1IxapkThZb5znbMf5s6lhCDPrzLbPDeyJQiUgs1UCrHZGUamw1e8R95S_ti4ELKA79pbZaKbZmuSUrLlL4Tmb6XorSa4kTztAg9J2Z8dUM_9qatDsO6xB5UhYGXgLc0KSNIjia3X0ucx5HppEimU8RBJJGrruDoy1fApkFuu7kwv-CAL7hfIQwPHENTpGbVz-hetDBcs7UILFuRDLbFzYPDe09iFlJ0NVcQOcLejSfgckibTQAld5jwg3Fzk_zfmoeQOFMWISQOnT3dFD6YRVMI3hnaFW19ve_kuUnMSmm2Ne9i9BHzqexct3WczLffMnBLcJkXd2LW3vnPatlKs7BffX0EmCfRMhgi8c-mhNMbNm23Hq4jM4q6ZHgghxeWQl0Pz3CDoACCoHOrtZ6RhnZX7BPtI5EzWd5F6BGTxLOrdpwh0YmGHO6VMehSwuTqRAffciAc5ZJn7Ism7CXjqUEuexeNS9odBSqv3BS-uGmLmDdqQNlDBj8tFJpu2AWRszoT2CiEEp8GCMav5AxpDelVzY5fvOEkQBg0vJn3O10JGujpB7iVMpzg-C8PWcfzSPQQw32eLGCZSeXZfJrim1vETntMClRRF7dJQRjUGfqQTMWa5jbVR8c_RGH-SWLpEsaM_EOB2f-GL0yFm7zisfu0vCBfVVJ37gLK-IB3JxPIQwSRSg0V-DRlhCzccmov8hn1Uf5v1w0HykNFPN7U6RhA9Jp67yrqwLoVNW_aYIquxVRzO-WmoeMsGkex72BaStTgEDuFifKQKcnbHLx5gT2LE1UN9Jr69NXBZ_0ZdkDyAnjYhYdj56c#5EEB5C53",
            "user-agent: okhttp/4.10.0"
    })
    Call<ResponseBody> getSymBolKLine_4h(@Url String symbol);

    @POST("cgi-bin/webhook/send?key=30d3476d-cc51-4ab1-8e2e-8ad68488f41d")
    Call<ResponseBody> sendMessage(@Body JsonObject msg);


    @GET("spot/currency_pairs")
    @Headers(value = {"Accept:application/json", "Content-Type:application/json"})
    Call<ResponseBody> getGateIoSymbols();

    @GET
    @Headers(value = {"Accept:application/json", "Content-Type:application/json"})
    Call<ResponseBody> getSymBolKLine_4h_GateIo(@Url String symbol);

}
