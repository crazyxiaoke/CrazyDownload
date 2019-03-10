本项目实现了android多线程下载，支持断点下载。

## 1、添加依赖
### 1.1、在根目录下的build.gradle中添加以下代码：
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### 1.2、然后module中的build.gradle中添加以下代码：
```
    dependencies {
	        implementation 'com.github.crazyxiaoke:CrazyDownload:1.0.0'
	}
```

## 2、初始化
### 2.1、在Application的onCreate中进行初始化，添加以下代码：
```
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DownloadDispatch.getInstance().init(this);
    }
}
```
### 2.2、也可以通过DownloadConfig类来设置下载参数。如以下代码：
```
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DownloadConfig config = new DownloadConfig.Builder()
                .setMaxDownloadSize(3)  //最大下载数
                .setDownloadThreadSize(3) //每个下载任务开启的最大线程数
                .setDeamon(true)  //是否启动线程保护
                .setDefaultFilePath("/")  //文件保存路径
                .setKeepAliveTime(60)  //线程sleep下存活时间
                .setUnit(TimeUnit.MILLISECONDS) //时间单位
                .build(this);
        DownloadDispatch.getInstance().init(this, config);
    }
}
```
## 3、开始使用
```
    //开始下载
    public void download(String url, DownloadCallback callback) 
    //停止下载
    public void stop(String url);
```
