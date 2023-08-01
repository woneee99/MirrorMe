import React, { useState, useEffect } from 'react';

function CurrentDate(props) {
  const [currentTime, setCurrentTime] = useState(new Date());
  const [blink, setBlink] = useState(true);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000); // 1초마다 상태 업데이트

    const blinkInterval = setInterval(() => {
      setBlink(prevBlink => !prevBlink);
    }, 500); // 0.5초마다 상태 토글

    // 컴포넌트가 언마운트되면 interval 정리 (클리어)
    return () => {
      clearInterval(interval);
      clearInterval(blinkInterval);
    };
  }, []);

  const year = currentTime.getFullYear();
  const month = currentTime.getMonth() + 1;
  const date = currentTime.getDate();
  const week = ['일', '월', '화', '수', '목', '금', '토'];
  const dayOfWeek = week[currentTime.getDay()];

  const hour = currentTime.getHours();

  // 시계를 hh:mm 형태로 변환
  const hour24 = hour > 12 ? hour - 12 : hour;
  const paddedHour = hour24.toString().padStart(2, '0');
  const minute = currentTime.getMinutes();
  const paddedMinute = minute.toString().padStart(2, '0');

  const ampm = hour >= 12 ? 'PM' : 'AM';

  return (
    <div>
      <h1>
        {blink ? `${ampm} ${paddedHour}:${paddedMinute}` : `${ampm} ${paddedHour} ${paddedMinute}`}
      </h1>
      <h4>
        {year}년 {month}월 {date}일 {dayOfWeek}요일
      </h4>
    </div>
  );
}

export default CurrentDate;