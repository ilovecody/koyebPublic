# koyebPublic
測試網址:https://elderly-minna-raytest.koyeb.app  

## Rest-single
單體式架構  
啟動方式:直接執行MyRestApp.java即可

## Rest-dubbo
Controller與Service拆分的分散式架構  
啟動方式:  
1.先執行services包內的ServiceApp.java  
2.再執行controller包內的ControllerApp.java  
- utilities controller與service之間共用的工具類
- model DB entity
- common controller與service之間通訊所需的共用基礎類
- controller 只有controller與view
- services 只有service與repository、dao等

