package pl.blokaj.pokerbro.ui.items.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class ProfilePictureComponent(
    componentContext: ComponentContext,
    setPath: () -> Unit
): ComponentContext by componentContext {
    private val _profilePicturePath = MutableValue<String>("")
    val profilePicturePath: Value<String> get() = _profilePicturePath

    fun getImageString(): String {
        return ""
    }
}