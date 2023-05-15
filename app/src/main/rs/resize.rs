#pragma version(1)
#pragma rs java_package_name(com.example.diplomtest)

// Input and output allocation
rs_allocation input;
rs_allocation output;

// Input and output dimensions
int inputWidth;
int inputHeight;
int outputWidth;
int outputHeight;

void resize() {
    // Compute the scaling factors for x and y
    float scaleX = (float)inputWidth / (float)outputWidth;
    float scaleY = (float)inputHeight / (float)outputHeight;

    // Iterate over the output pixels
    for (int y = 0; y < outputHeight; y++) {
        for (int x = 0; x < outputWidth; x++) {
            // Compute the corresponding input pixel coordinates
            int inputX = (int)(x * scaleX);
            int inputY = (int)(y * scaleY);

            // Read the input pixel
            float4 pixel = rsUnpackColor8888(rsGetElementAt_uchar4(input, inputX, inputY));

            // Write the output pixel
            rsSetElementAt_uchar4(output, rsPackColorTo8888(pixel), x, y);
        }
    }
}
