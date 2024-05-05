package momosetkn

inline fun <E> E.applyIf(b: Boolean, block: () -> E): E {
    return if (b) {
        block()
    } else {
        this
    }
}
