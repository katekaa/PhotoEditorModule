// gray.rs
#pragma version(1)
#pragma rs java_package_name(com.example.resizemodule)
#pragma rs_fp_imprecise//rs_fp_relaxed//rs_fp_full//rs_fp_relaxed

const float3 gMonoMult = {0.299f, 0.587f, 0.114f};
/*
 * RenderScript kernel that performs grayscale manipulation
 */
uchar4 __attribute__((kernel)) gray(uchar4 in)
{
    // Transform the input pixel with a value range of [0, 255] to
    // a float4 vector with a range of [0.0f, 1.0f]
    float4 inF = rsUnpackColor8888(in);
    // Calculate the dot product of the rgb channels and the global constant we defined and
    // assign the result to each element in a float3 vector
    float3 outF = dot(inF.rgb, gMonoMult);
    // Transform the resulting color back to a uchar4 vector.
    // Since the input color is just a float3 instead of a float4 the alpha value will
    // be set to 255 or fully opaque.
    return rsPackColorTo8888(outF);
}