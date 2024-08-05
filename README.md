# Java-translator-T-bank

## Структура проекта Java-translator-T-bank

### 1. Используемые технологии

- Spring Boot 2.0 и выше (Spring Web, Spring Data JPA)
- H2 Database (JDBC)
- Maven
- Yandex Translate API
- RestTemplate

### 2. Файловая структура основной директории проекта

```bash
Java-translator-T-bank/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── App/
│   │   │       ├── config/
│   │   │       │   └── AppConfig.java
│   │   │       ├── controller/
│   │   │       │   └── TranslationController.java
│   │   │       ├── exception/
│   │   │       │   └── TranslationException.java
│   │   │       ├── model/
│   │   │       │   └── TranslationRecord.java
│   │   │       ├── repository/
│   │   │       │   └── TranslationRepository.java
│   │   │       ├── servise/
│   │   │       │   └── TranslationService.java
│   │   │       └── TranslationAppApplication.java
│   │   ├── resources/
│   │   │   └── application.properties
│   └── test/java/com/example/demo/
│   │   └── DemoApplicationTests.java
├── .gitignore
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Запуск и тестирование

### 1. Клонируйте репозиторий проекта

Для того чтобы склонировать этот репозиторий, выполните следующую команду в вашей терминальной оболочке:

```bash
git clone https://github.com/Brendow271/Java-translator-T-bank.git
```

После успешного клонирования, перейдите в директорию проекта с помощью команды:

```bash
cd Java-translator-T-bank
```

### 2. Получите Yandex Translate API

#### Документация доступна по ссылке [Yandex Translate](https://yandex.cloud/ru/docs/translate/).

#### Пошаговое решение для Windows PowerShell:

##### Настройка сервисного аккаунта

- Перейдите в [консоль управления](https://console.yandex.cloud/).
- В фолдере, можно в дефолтном, заведите Платёжный аккаунт.
- Убедитесь, что статус Active.
- Перейдите во вкладку Сервисные аккаунты и Создайте сервисный аккаунт с добавленной ему ролью ai.translate.user

##### Получение IAM-токена

- Установите интерфейс командной строки Yandex Cloud в Windows PowerShell:
```bash
iex (New-Object System.Net.WebClient).DownloadString('https://storage.yandexcloud.net/yandexcloud-yc/install.ps1')
```
- Введите:
```bash
yc init
```
- Перейдите по появившейся ссылке и получите OAuth token (откроется в браузере) и скопируйте его.
- Введите OAuth token. Вы получите информацию о вашем облаке и список фолдеров. Выберите фолдер, в котором у Вас был настроен Сервисный аккаунт и скопируйте его id. Вставьте его в файл проекта `application.properties` после yandex.folder.id=
- Будет предложен выбор Compute zone. Выберите на своё усмотрение.
- Далее, введите:
```bash
yc iam key create --service-account-name translator-java --output key.json
```
где после --service-account-name, вместо предложенного translator-java, введите Имя ранее настроенного Сервисного аккаунта.
- Далее, создайте профиль, например с таким именем:
```bash
yc config profile create my-robot-profile
```
- Далее, введите:
```bash
yc config set service-account-key key.json
```
- Далее, введите:
```bash
yc iam create-token
```
И Вам будет выведен Ваш IAM Token, который надо подставить в файл `application.properties` после yandex.api.key=

### 3. Запуск и использование приложения

Сборка проекта:
```bash
mvn clean install
```

Для запуска проекта используйте команду:
```bash
mvn spring-boot:run
```
из корневой директории.

#### Доступ к базе данных

- Укажите не занятый у вас порт в `application.properties` после server.port= или же оставьте как в коде.
- Перейдите в своём браузере на http://localhost:8082/h2-console (если не меняли порт, или же подставьте свой).
- Введите из `application.properties` url в JDBC URL:, логин и пароль в соответствующие поля H2 Console.
- Нажмите Connect.
- Впишите SQL-запрос:
```bash
SELECT * FROM TRANSLATION_RECORD;
```
и нажмите Run.

#### Использование приложения с Postman

- Создайте новую коллекцию Create new collection.
- Создайте новый реквест Add request. Выберите метод POST. Введите в поле http://localhost:8082/api/translate. Задайте параметры в Body (raw): texts (Максимальная длина которого 100000 символов), sourceLanguageCode, targetLanguageCode. По аналогии с примерами ниже.

## Пример использования

### Postman

#### Пример 200 Оk запроса
![200_ok](https://github.com/user-attachments/assets/db1f9fe3-72a2-4401-8921-a0c366b82240)
#### Пример 400 Bad_request запроса
![400_bad_request](https://github.com/user-attachments/assets/d09b0f53-d6a3-47ff-bc6a-db6aea70d799)
#### Пример 400 Invalid_language запроса
![400_invalid_language](https://github.com/user-attachments/assets/789e1af9-2db2-4aed-9be1-6858f30ab31b)
#### Пример 401 Unauthorized запроса
![401_Unauthorized](https://github.com/user-attachments/assets/3ea80f0b-dd6c-414e-a9cf-272c656fe1e8)

### H2 Console
#### Пример базы данных после нескольких запросов
![database_exapmle](https://github.com/user-attachments/assets/621c51bf-0789-4f19-add6-ba916b12fa85)
