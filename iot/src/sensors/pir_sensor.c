#include <stdio.h>
#include <gpiod.h>
#include <unistd.h>
#include <stdlib.h>

// Jetson Nano GPIO 매핑 정보
// 참고: Jetson Nano에서는 `libgpiod`를 사용할 때 `gpiochip0` 라인 번호를 사용해야 합니다.
// 아래는 BOARD 핀 번호와 gpiochip0 라인 번호 매핑 정보입니다.
//
// BOARD 핀 번호 | 라즈베리 파이 BCM 번호 | Jetson Nano `gpiochip0` 라인 번호
// ---------------------------------------------------------------------------
// 7             | BCM 4                  | 216
// 11            | BCM 17                 | 50
// 12            | BCM 18                 | 79
// 13            | BCM 27                 | 14
// 15            | BCM 22                 | 194
// 16            | BCM 23                 | 232
// 18            | BCM 24                 | 15
// 19            | BCM 10                 | 16
// 21            | BCM 9                  | 17
// 22            | BCM 25                 | 13
// 23            | BCM 11                 | 18
// 24            | BCM 8                  | 19
// 26            | BCM 7                  | 20
// 29            | BCM 5                  | 149
// 31            | BCM 6                  | 200
// 32            | BCM 12                 | 168
// 33            | BCM 13                 | 38
// 35            | BCM 19                 | 76
// 36            | BCM 16                 | 51
// 37            | BCM 26                 | 12
// 38            | BCM 20                 | 77
// 40            | BCM 21                 | 78
//
// 예를 들어, BOARD 핀 11 (BCM 17)로 LED를 제어하려면 `gpiochip0`의 라인 번호 50을 사용합니다.
// PIR 센서를 BOARD 핀 7 (BCM 4)에 연결한 경우, `gpiochip0`의 라인 번호 216을 사용합니다.


#define CHIP_NAME "gpiochip0"   // Jetson Nano의 GPIO 칩 이름
#define LED_PIN 50              // LED가 연결된 핀 (BOARD 핀 11, gpiochip0 라인 50)
#define PIR_PIN 216             // PIR 센서가 연결된 핀 (BOARD 핀 7, gpiochip0 라인 216)

int main() {
    struct gpiod_chip *chip;
    struct gpiod_line *led_line, *pir_line;
    int pir_state;

    // GPIO 칩과 라인 초기화
    chip = gpiod_chip_open_by_name(CHIP_NAME);
    if (!chip) {
        perror("Failed to open GPIO chip");
        return 1;
    }

    // LED 핀 설정 (출력)
    led_line = gpiod_chip_get_line(chip, LED_PIN);  // gpiochip0 라인 번호 50 (BOARD 핀 11)
    if (!led_line) {
        perror("Failed to get LED GPIO line");
        gpiod_chip_close(chip);
        return 1;
    }
    if (gpiod_line_request_output(led_line, "pir_led_control", 0) < 0) {
        perror("Failed to request LED line as output");
        gpiod_chip_close(chip);
        return 1;
    }

    // PIR 센서 핀 설정 (입력)
    pir_line = gpiod_chip_get_line(chip, PIR_PIN);  // gpiochip0 라인 번호 216 (BOARD 핀 7)
    if (!pir_line) {
        perror("Failed to get PIR GPIO line");
        gpiod_chip_close(chip);
        return 1;
    }
    if (gpiod_line_request_input(pir_line, "pir_led_control") < 0) {
        perror("Failed to request PIR line as input");
        gpiod_chip_close(chip);
        return 1;
    }

    // PIR 센서 상태 체크 및 LED 제어
    while (1) {
        pir_state = gpiod_line_get_value(pir_line);  // PIR 센서 값 읽기
        if (pir_state < 0) {
            perror("Failed to read PIR line value");
            break;
        }

        if (pir_state == 1) {
            gpiod_line_set_value(led_line, 1);  // LED ON (동작 감지 시)
            printf("Motion Detected\n");
        } else {
            gpiod_line_set_value(led_line, 0);  // LED OFF (동작 미감지 시)
            printf("Motion Not Detected\n");
        }
        sleep(1);  // 1초 대기
    }

    // 자원 해제
    gpiod_chip_close(chip);
    return 0;
}

