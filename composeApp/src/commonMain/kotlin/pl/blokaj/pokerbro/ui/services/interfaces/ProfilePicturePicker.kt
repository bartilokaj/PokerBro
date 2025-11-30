package pl.blokaj.pokerbro.ui.services.interfaces

interface ProfilePicturePicker {
    fun pickProfilePicture(onPicked: (String) -> Unit)
}