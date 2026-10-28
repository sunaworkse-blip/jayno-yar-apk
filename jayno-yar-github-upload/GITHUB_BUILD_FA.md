# ساخت APK جاینو یار با GitHub Actions

1. این ZIP را استخراج کنید.
2. در GitHub یک مخزن **Private** با نام `jayno-yar-apk` بسازید.
3. فایل‌های داخل این پوشه را در ریشه مخزن آپلود و commit کنید؛ پوشه `.github/workflows` نیز باید آپلود شود.
4. وارد تب **Actions** شوید و workflow با نام **Build Jayno Yar APK** را انتخاب کنید.
5. روی **Run workflow** بزنید.
6. پس از اتمام موفق، از صفحه اجرای workflow بخش **Artifacts**، فایل `jayno-yar-debug-apk` را دانلود کنید.
7. ZIP دانلودشده را استخراج کنید؛ فایل `app-debug.apk` داخل آن است.

این workflow یک APK دیباگ امضاشده با کلید دیباگ می‌سازد؛ برای نصب آزمایشی مناسب است، نه انتشار نهایی فروشگاهی.
