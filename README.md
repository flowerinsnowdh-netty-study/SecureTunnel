# 重要提醒
**学习项目，还没有写完**

# SecureTunnel
一款自己研发的基于 Java netty 的加密信道

# 握手细节
1.  (C->S) 发送魔数 (0x360B7CC2)
2.  (C)    生成客户端临时密钥对 (EC 256)
3.  (C->S) 发送客户端临时公钥 (0x01 ClientHello)
4.  (S<-C) 接收客户端临时公钥
5.  (S->C) 发送魔数 (0x360B7CC2)
6.  (S)    生成服务器临时密钥对 (EC 256)
7.  (S->C) 发送服务器临时公钥 (0x02 ServerHello)
8.  (S)    通过客户端临时公钥、服务器临时私钥合成共享安全密钥
9.  (C)    通过服务器临时公钥、客户端临时私钥合成共享安全密钥
10. (S)    生成C2S GCM参数 (128位)，并通过共享安全密钥加密 (AES/ECB/PKCS7Padding)
11. (S->C) 发送加密后的C2S GCM参数 (0x03 C2SGCMParameter)
12. (C<-S) 接收并解密C2S GCM参数
13. (C)    生成S2C GCM参数 (128位)，并通过共享安全密钥与C2S GCM参数加密 (AES/GCM/NoPadding tLen=96)
14. (C->S) 发送加密后的S2C GCM参数 (0x04 S2CGCMParameter)
15. (S<-C) 接收并解密加密后的S2C GCM参数
16. (C/S)  从现在开始通信内容内容均使用共享安全密钥和对应GCM参数加解密 (AES/GCM/NoPadding tLen=96)
17. (S->C) 发送服务器 Host 公钥 (EC 256)
18. (C)    判断是否接受该 Host 公钥
19. (C)    随机生成 signatureID (128位)
20. (C->S) 发送 signatureID，要求服务器对其签名 (0x05 SignatureRequest)
21. (S->C) 签名客户端临时公钥+服务器临时公钥+初始 C2S GCM参数+初始 S2C GCM参数+signatureID (ECDSA+SM3)，并发送 (0x06 HandshakeSignature)
22. (C)    验证签名，判断是否接受
23. (C->S) 发送结果 (0x07 HandshakeOver)

每次通过对应 GCM 参数加密后，使用 SM3 刷新该参数