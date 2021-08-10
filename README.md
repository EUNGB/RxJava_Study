# RxJava_Study
RxJava Study

## Reactive Programing
프로그램 자신의 주변 환경과 끊임없이 상호작용을 하는데 프로그램이 주도하는 것이 아니라 환경이 변하면 이벤트를 받아 동작 (데이터의 흐름과 전달에 관한 프로그래밍 패러다임)
데이터 흐름을 먼저 정의하고 데이터가 변경되었을 때 연관되는 함수나 메서드가 업데이트 되는 방식

### 사용하는 이유
사용자 경험을 향상시키고 싶어함 ⇒ 네트워크 운영을 위한 비동기 작업 필요
안드로이드의 어려움 = 비동기 처리 및 에러 핸들링, 수많은 핸들러 및 콜백으로 발생하는 디버깅 문제, 이벤트 중복 실행 등

## ReactiveX
비동기 프로그래밍과 Observable 시퀀스를 이용해 이벤트를 처리하기 위한 라이브러리

###장점
- 효율적으로 신속하게 비동기 처리를 도와줌
- 함수형 프로그래밍을 일부 지원함
- 옵저버패턴을 사용함


## Observable의 주요 이벤트
- onNext(item T) : 값을 전달할 때 호출하여 값을 넘겨줌
- onError(e: Throwable) : 에러가 발생하면 호출 함
- onSubscribed(d: Disposable) : 구독을 신청하면 호출
- onComplete() : 가지고 있는 값을 모두 전달하면 호출 함

```
fun main() {
    val list = listOf(1, 2, 3, 4, 5)
		
    // 리스트(데이터)로 observable 인스턴스 생성
    val observable: Observable<Int> = list.toObservable()

    // observable 인스턴스 구독
    observable.subscribeBy(
	onNext = { println(it) },
	onError = { it.printStackTrace() },
	onComplete = { println("Complete!" }
    )
}

// 실행결과
1
2
3
4
5
Complete!
```

소비자 등록 방식

1. Observer 방식 : Observer 인터페이스를 구현한 객체를 subscribe해서 소비자를 추가
subscribe의 return type은 Unit

```kotlin
val observer = object : Observer<Int> {
    override fun onComplete() {
     	// Observable이 완료된 경우
    }
	
    override fun onSubscribe(d: Disposable) {
    	// Observable이 데이터 전달할 준비가 되었을 때.
    	// 작업 취소를 위한 Disposable에 대한 레퍼런스를 여기서 받음
    }
   
    override fun onNext(t: Int) {
        // Observable이 데이터를 전달할 때 호출
    }
    
    override fun onError(e: Throwable) {
       // Observable이 에러를 전달할 때 호출. Error시 Complete없이 종료다.
    }
}
Observable.just(1, 2, 3, 4).subscribe(observer)
```

2. Consumer 방식 : 각각의 Consumer를 subscribe해서 소비자를 추가
subscribe의 return  type은 Disposable

```kotlin
val disposable: Disposable = Observable.just(1, 2, 3, 4)
     .subscribe(
        { println("onNext $it") }, // onNext: Consumer
        { println("onError") }, // onError: Consumer
        { println("onComplete") }, // onComplete: Consumer
        { println("onSubscribe") } // onSubscribe: Consumer
     )
```

### Observable
0개에서 n개의 데이터를 전달하는 생산자


### Single
오직 1개의 데이터를 전달하는 생산자
결과가 1개의 데이터 또는 실패 인 경우 사용 (Http)

```kotlin
// SingleObserver를 구현해 전달
Single.just(1)
    .subscribe(
	{ println("onSuccess $it") },
	{ println("onError") }
    )
```
<img src="/img/single.png" width="500px" height="50px"></img><br/>

### Completable
0개의 데이터를 전달하는 생산자
DB에 insert, update와 같이 데이터가 필요 없이 성공 or 실패인 경우 사용

```kotlin
// CompletableObserver 구현해 전달
Completable.complete()
	.subscribe(
	    { println("onComplete") },
	    { println("onError") }
	)

Completable.fromAction(::completeAction)
        .subscribe(
            { println("onComplete")},
            { println("onError")}
        )

fun completeAction() {
    println("Hello, World")
    // DB insert or Update
}
```



### Maybe
0개 또는 1개의 데이터를 전달하는 생산자

```kotlin
Maybe.just("Hello World")
    .subscribe(
         { println("onSuccess $it") },
         { println("onError ${it.message}")  },
         { println("onComplete") }
    )

Maybe.empty<Unit>()
     .subscribe(
         { println("onSuccess $it") },
         { println("onError ${it.message}")  },
         { println("onComplete") }
     )
```

### Flowable
데이터의 발행 속도가 구독자의 처리속도보다 크게 빠를때 사용
BackPressure Issue를 처리하는 방법을 설정할 수 있음
LiveData와 연계할 수 있음

```kotlin
// FloawbleSubscriber
Flowable.just(1, 2, 3, 4)
     .subscribe(
	{ println("onNext $it") },
	{ println("onError") },
	{ println("onComplete") },
	{ println("onSubscribe") }
      )
```

## RxJava 연산자

### Create
함수 내부에서 emitter가 직접 onNext, onComplete등으로 데이터를 전달하는 연산자

```kotlin
Observable.create<String> { emitter -> 
	emitter.onNext("Hello")
	emitter.onNext("RxJava")
	emitter.onComplete()
    }.subscribe { println(it) }

Observable.create<String> { emitter -> 
	emitter.onNext("Hello")
	emitter.onNext("RxJava")
	emitter.onError(Throwable())
	emitter.onComplete()
    }.subscribe { println(it) }
```


### defer
ObservableSource를 리턴하는 Callable을 받는 연산자

```kotlin
Observable.defer {
    Observable.create<String> { emitter ->
	emitter.onComplete()
    }
}.subscribe(::println)
```

### from

Array, Iterable, Callable로부터 Observable을 만드는 연산자

```kotlin
val items = arrayOf(1, 2, 3, 4)
Observable.fromArray(*items).subscribe { println(it) }
```



### interval

주어진 주기대로 0부터 1씩 증가된 값을 만드는 연산자

interval은 별도의 스레드에서 처리하기 때문에 Thread.sleep()을 통해 기다림

```kotlin
Observable.interval(100, TimeUnit.MILLSECONDS)
	.subscribe(::println)
Thread.sleep(300)
```


### just

최대 10개의 데이터를 전달하는 연산자

```kotlin
Observable.just(1, 2, 3).subscribe(::println)
```

### range

range(start, count) : start부터 count만큼 1씩 증가한 데이터를 전달하는 연산자

```kotlin
Observable.range(3, 10).subscribe(::println)
```


### repeat

Observable을 지정한 횟수만큼 반복시키는 연산자
서버가 살아있는지 확인 할 때 많이 사용(ping)

```kotlin
val observable = Observable.just("Hello", "World").repeat(3)
observable.subscribe(::println)
```


### timer

정해진 시간 후 0을 전달하는 Observable을 반환
interval과 비슷하지만, 일정 시간이 지난 후 한 개의 데이터를 발행하고 onComplete 이벤트 발생

```kotlin
println("Start TS = ${System.currentTimeMillis()}")
    Observable.timer(1000, MILLISECONDS).subscribe {
        println("Start TS = ${System.currentTimeMillis()} $it")
    }
Thread.sleep(5000) // 별도의 스레드에서 처리하기 때문에 기다림
```


## 반환 연산자

### map

데이터를 변환하는 연산자

```kotlin
Observable.fromIterable(0..5)
	.map { "item = $it" }
	.subscirbe(::println)
```


## 필터링 연산자

### filter

특정 조건에 맞는 데이터만 전달

```kotlin
Observable.just(10, "100", true, "java",  19.4)
        .filter { it == "java"}
        .subscribe(::println)
```


### ofType

특정 타입에 맞는 데이터만 전달, 전달 시 typecasting이 되어 있음

```kotlin
Observable.just(10, "100", true, "java",  19.4)
	.ofType(String::class.java)
	.subscirbe(::println)
```
