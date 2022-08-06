### 1.generte ssl key & ssl cert
``
openssl req -x509 -newkey rsa:4096 -nodes -keyout tls.key -out tls.crt -days 90
``
### 2.create secret tls
``
kubectl  create secret tls https-cert-secret --key tls.key --cert tls.crt
``
### 3.create ingress with tls
``
kubectl create ingress native-boot-ingress --class=nginx --rule=boot-native.com/*=native-boot-service:8089,tls=https-cert-secret
``