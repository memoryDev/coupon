import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [userId, setUserId] = useState('');
  const [status, setStatus] = useState('IDLE'); // IDLE, WAITING, READY, SUCCESS, ERROR
  const [queueInfo, setQueueInfo] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [intervalId, setIntervalId] = useState(null);

  // 쿠폰 발급 시도
const tryIssueCoupon = async () => {
  if (!userId) {
    alert('유저 ID를 입력하세요');
    return;
  }

  try {
    const response = await axios.post(`http://localhost:8080/api/v4/coupons/issue/${userId}`);
    const data = response.data;

    if (data.status === 'SUCCESS') {
      // 즉시 발급 성공
      setStatus('SUCCESS');
    } else if (data.status === 'QUEUE') {
      // 대기열 진입
      setStatus('WAITING');
      setQueueInfo({ rank: data.rank });
      checkQueueStatus(); // 주기적 확인 시작
    }
  } catch (error) {
    setStatus('ERROR');
    setErrorMessage(error.response?.data?.message || error.response?.data || '쿠폰 발급 실패');
  }
};

  // 대기열 상태 확인 (3초마다)
  const checkQueueStatus = () => {
    const interval = setInterval(async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/v4/queue/status/${userId}`);
        const data = response.data;
        
        setQueueInfo(data);

        // 발급 가능하면 자동 발급
        if (data.canIssue) {
          clearInterval(interval);
          await issueCouponFromQueue();
        }
      } catch (error) {
        clearInterval(interval);
        setStatus('ERROR');
        setErrorMessage('상태 확인 실패');
      }
    }, 3000);
    
    setIntervalId(interval);
  };

  // 대기열에서 쿠폰 발급
  const issueCouponFromQueue = async () => {
    try {
      setStatus('READY');
      await axios.post(`http://localhost:8080/api/v4/coupons/issue-from-queue/${userId}`);
      setStatus('SUCCESS');
    } catch (error) {
      setStatus('ERROR');
      setErrorMessage(error.response?.data?.message || error.response?.data || '쿠폰 발급 실패');
    }
  };

  // 초기화
  const reset = () => {
    if (intervalId) {
      clearInterval(intervalId);
    }
    setUserId('');
    setStatus('IDLE');
    setQueueInfo(null);
    setErrorMessage('');
    setIntervalId(null);
  };

  return (
    <div className="App">
      <div className="container">
        <h1>🎟️ 선착순 쿠폰 발급</h1>

        {status === 'IDLE' && (
          <div className="input-section">
            <input
              type="number"
              placeholder="유저 ID 입력"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
            />
            <button onClick={tryIssueCoupon}>쿠폰 받기</button>
          </div>
        )}

        {status === 'WAITING' && queueInfo && (
          <div className="waiting-section">
            <div className="spinner"></div>
            <h2>대기 중...</h2>
            <p className="rank">현재 순번: <strong>{queueInfo.rank}번</strong></p>
            <p className="info">쿠폰이 소진되어 대기열에 등록되었습니다</p>
          </div>
        )}

        {status === 'READY' && (
          <div className="ready-section">
            <h2>발급 중...</h2>
          </div>
        )}

        {status === 'SUCCESS' && (
          <div className="success-section">
            <div className="checkmark">✓</div>
            <h2>쿠폰 발급 완료!</h2>
            <p>쿠폰이 성공적으로 발급되었습니다.</p>
            <button onClick={reset}>다시 하기</button>
          </div>
        )}

        {status === 'ERROR' && (
          <div className="error-section">
            <h2>❌ 오류 발생</h2>
            <p>{errorMessage}</p>
            <button onClick={reset}>다시 시도</button>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;