#pragma version(1)
#pragma rs java_package_name(com.example.resizemodule)

rs_allocation gIn;
rs_allocation gOut;

void root(const uchar4* in, uchar4* out) {
    float4 pixelf = rsUnpackColor8888(*in);
    float gray = (0.299f * pixelf.r + 0.587f * pixelf.g + 0.114f * pixelf.b);
    out->rgb = gray;
    out->a = pixelf.a;
}


