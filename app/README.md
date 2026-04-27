com.example.lifetrack
┣ 📜 MainActivity.kt

┣ 📂 data
┃ ┣ 📂 model
┃ ┃ ┣ Note.kt
┃ ┃ ┣ Reminder.kt
┃ ┃ ┗ Memory.kt
┃ ┣ 📂 repository
┃ ┃ ┣ NoteRepository.kt
┃ ┃ ┣ ReminderRepository.kt
┃ ┃ ┗ MemoryRepository.kt
┃ ┗ 📂 auth
┃   ┗ AuthRepository.kt   ✅

┣ 📂 viewmodel
┃ ┣ NoteViewModel.kt
┃ ┣ ReminderViewModel.kt
┃ ┣ MemoryViewModel.kt
┃ ┗ AuthViewModel.kt

┣ 📂 ui
┃ ┣ 📂 screens
┃ ┃ ┣ 📂 auth
┃ ┃ ┃ ┣ LoginScreen.kt
┃ ┃ ┃ ┣ RegisterScreen.kt
┃ ┃ ┃ ┗ ForgotPasswordScreen.kt
┃ ┃ ┣ HomeScreen.kt
┃ ┃ ┣ NoteScreen.kt
┃ ┃ ┣ ReminderScreen.kt
┃ ┃ ┗ MemoryScreen.kt
┃ ┣ 📂 components
┃ ┃ ┗ NoteCard.kt
┃ ┗ 📂 theme

┣ 📂 navigation
┃ ┣ AppNavigation.kt
┃ ┗ Routes.kt   ✅

┣ 📂 utils
┃ ┣ Constants.kt
┃ ┗ DateUtils.kt