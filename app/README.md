📦 com.example.lifetrack
┣ 📂 MainActivity.kt
┣ 📂 auth
┃ ┗ 📜 AuthManager.kt           // Firebase Auth logic
┣ 📂 data
┃ ┣ 📂 model                    // Data Classes (Schema)
┃ ┃ ┣ 📜 Note.kt
┃ ┃ ┣ 📜 Reminder.kt
┃ ┃ ┗ 📜 Memory.kt
┃ ┗ 📂 repository               // Firestore CRUD logic
┃   ┣ 📜 NoteRepository.kt      // Path: users/{uid}/notes
┃   ┣ 📜 ReminderRepository.kt  // Path: users/{uid}/reminders
┃   ┗ 📜 MemoryRepository.kt    // Path: users/{uid}/memories
┣ 📂 viewmodel                  // 🟢 ALL ViewModels go here
┃ ┣ 📜 NoteViewModel.kt
┃ ┣ 📜 ReminderViewModel.kt
┃ ┣ 📜 MemoryViewModel.kt
┃ ┗ 📜 AuthViewModel.kt
┣ 📂 ui                          // Only Composable UI files
┃ ┣ 📂 screens
┃ ┃ ┣ 📜 AuthScreen
┃ ┃ ┃ ┣ 📜 LoginScreen.kt
┃ ┃ ┃ ┣ 📜 RegisterScreen.kt
┃ ┃ ┃ ┣ 📜 ForgotPasswordScreen.kt
┃ ┃ ┣ 📜 HomeScreen.kt
┃ ┃ ┣ 📜 NoteScreen.kt
┃ ┃ ┣ 📜 ReminderScreen.kt
┃ ┃ ┗ 📜 MemoryScreen.kt
┃ ┣ 📂 components               // Reusable widgets (Cards, Buttons)
┃ ┃ ┗ 📜 NoteCard.kt
┃ ┗ 📂 theme                    // Design system (Color, Type)
┣ 📂 navigation
┃ ┗ 📜 AppNavigation.kt         // NavHost and Routes
┗ 📂 utils
┣ 📜 Constants.kt               // Firestore collection keys
┗ 📜 DateUtils.kt               // Date formatting