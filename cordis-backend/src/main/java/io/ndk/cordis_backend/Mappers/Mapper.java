package io.ndk.cordis_backend.Mappers;

public interface Mapper<A,B>{
    B mapTo(A a);
    A mapFrom(B b);
}
