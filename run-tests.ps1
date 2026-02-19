# run-tests.ps1
Write-Host "Запуск всех тестов..." -ForegroundColor Cyan

# Выполняем clean test
$output = & .\gradlew.bat clean test 2>&1
$exitCode = $LASTEXITCODE

# Выводим последние 20 строк для контекста
Write-Host "`nПоследние строки вывода:" -ForegroundColor Yellow
$output[-20..-1] | ForEach-Object { Write-Host $_ }

# Проверяем результат
if ($exitCode -eq 0) {
    Write-Host "`n✅ Все тесты успешно пройдены!" -ForegroundColor Green
} else {
    Write-Host "`n❌ Некоторые тесты завершились с ошибкой." -ForegroundColor Red
}

# Показываем путь к отчёту
$reportPath = "api\build\reports\tests\test\index.html"
if (Test-Path $reportPath) {
    Write-Host "`nПодробный отчёт доступен по адресу:" -ForegroundColor Cyan
    Write-Host "file:///$((Get-Item $reportPath).FullName)" -ForegroundColor White
    Write-Host "Можете открыть его в браузере." -ForegroundColor Cyan
} else {
    Write-Host "`nОтчёт не найден." -ForegroundColor Red
}

# Пауза, чтобы окно не закрылось
Read-Host "`nНажмите Enter для выхода"