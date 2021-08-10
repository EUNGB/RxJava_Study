package com.eungb.rxjavastudy

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.observers.DisposableMaybeObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

fun main() {

    // Single
    Single.just(1)
        .subscribe(
            { println("onSuccess $it") },
            { println("onError") }
        )

    // Maybe
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

    // Completable
    Completable.fromAction(::completeAction)
        .subscribe(
            { println("onComplete") },
            { println("onError") }
        )

   // create
    Observable.create<String> { emitter ->
        emitter.onNext("Hello")
        emitter.onNext("RxJava")
        emitter.onError(Throwable())
        emitter.onComplete()
    }.subscribe { println(it) }

    // from
    val items = arrayOf(1, 2, 3, 4)
    Observable.fromArray(*items).subscribe { println(it) }

    // interval
    Observable.interval(100L, MILLISECONDS).subscribe(::println)
    Thread.sleep(300)

    // timer
    println("Start TS = ${System.currentTimeMillis()}")
    Observable.timer(1000, MILLISECONDS).subscribe {
        println("Start TS = ${System.currentTimeMillis()} $it")
    }
    Thread.sleep(5000)

    // range
    Observable.range(3, 10).subscribe(::println)

    // repeat
    Observable.just("Hello", "World").repeat(3).subscribe(::println)

    // map
    Observable.fromIterable(0..5)
        .map { "item = $it" }
        .subscribe(::println)

    // ofType
    Observable.just(10, "100", true, "java",  19.4)
        .ofType(String::class.java)
        .subscribe(::println)

    // filter
    Observable.just(10, "100", true, "java",  19.4)
        .filter { it == "java"}
        .subscribe(::println)

}

fun completeAction() {
    println("Hello, World")
    // DB insert or Update
}
