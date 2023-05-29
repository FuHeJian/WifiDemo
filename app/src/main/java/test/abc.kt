package test

import kotlin.coroutines.CoroutineContext

/**
 *
 *
 * test
 */
public data class ComPa(val name: String) : ComPaa(ComPa) {
    public companion object Key : MKey {
        override fun o()=1

    }
}

public abstract class ComPaa(public override var a: MKey?):P() {

}

public interface MKey{
    fun o():Int
}

public open class P{
    public open var a: MKey? = null
}