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
// PIR 센서를 BOARD 핀 7 (BCM 4)에 연결한 경우, `gpiochip0`의 라인 번호 216을 사용합니다.

#define CHIP_NAME "gpiochip0"   // Jetson Nano의 GPIO 칩 이름
#define PIR_PIN 216             // PIR 센서가 연결된 핀 (BOARD 핀 7, gpiochip0 라인 216)

int detect_motion() {
    struct gpiod_chip *chip;
    struct gpiod_line *pir_line;
    int pir_state, last_pir_state = 0;  // 현재 상태와 이전 상태를 비교하기 위해 초기화

    // GPIO 칩과 라인 초기화
    chip = gpiod_chip_open_by_name(CHIP_NAME);
    if (!chip) {
        perror("Failed to open GPIO chip");
        return 1;
    }

    // PIR 센서 핀 설정 (입력)
    pir_line = gpiod_chip_get_line(chip, PIR_PIN);
    if (!pir_line) {
        perror("Failed to get PIR GPIO line");
        gpiod_chip_close(chip);
        return 1;
    }

    if (gpiod_line_request_input(pir_line, "pir_detect_once") < 0) {
        perror("Failed to request PIR line as input");
        gpiod_chip_close(chip);
        return 1;
    }

    // PIR 센서 상태 변화 체크
    pir_state = gpiod_line_get_value(pir_line);  // PIR 센서 값 읽기
    if (pir_state < 0) {
        perror("Failed to read PIR line value");
    }
		
    // 자원 해제
    gpiod_chip_close(chip);
    return pir_state;
}

