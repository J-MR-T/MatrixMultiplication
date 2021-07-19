# MatrixMultiplication
A CLI Matrix Multiplication program (which has morphed into a general matrix calculator I think) written in Kotlin.

# Features
Interactive command line utility which can parse arithmetic Expressions such as 
- A\*B (A times B)
- A^T (A transposed)
- Id3 (Identity Matrix of the third Dimension)
- Scalar- and Matrix-Variables like this:
  - A=readMatrix()
  - B=A+B
  - k=readScalar()
- A\*k (Matrix times scalar)
- A^k (Matrix to the power of scalar)
- (A+B) (Parentheses)
- Scalar Operations:
  - 5+3
  - 5*3
  - 5^3
  - 5/3
- Automatic conversion between floating point numbers and integers:
  - Let V=(0.5,0.5) (input via V=readMatrix()), then V\*2 will not ouput (1.0,1.0) but (1,1)
- Everything combined: (A+(C=B))^T\*Id3\*(5^(3\*3+1))

As well as a seperate environment to handle binary linear encoding and decoding.

# Credit
https://github.com/h0tk3y/better-parse is licensed under the Apache 2.0 license, see ThirdPartyLicenses/APACHE-LICENSE-2.0.txt
