import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const LoginPage: React.FC = () => {
  const [formData, setFormData] = useState({
    userId: "",
  });
  const navigate = useNavigate();
  const [error, setError] = useState("");

  // 입력값이 변경될 때 formData 업데이트
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // 로그인 버튼 클릭 시 동작
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.userId.trim() === "") {
      setError("일련번호를 입력해주세요.");
    } else {
      setError("");
      // 로그인 성공 시 다음 페이지로 이동 (예시: /home) 수정
      navigate("/home");
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="bg-white bg-opacity-80 rounded-xl shadow-lg text-center w-2/5 px-28 py-12">
        <h2 className="text-[35px] font-bold mb-6">로그인</h2>
        <form onSubmit={handleSubmit} className="space-y-12">
          <div className="space-y-2">
            <input
              type="text"
              name="userId"
              value={formData.userId}
              onChange={handleChange}
              placeholder="일련번호  "
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
              style={{ boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)" }}
            />
            <p className="text-red-500 text-sm h-1 text-left ml-2">{error}</p>
          </div>
          <button
            type="submit"
            className="px-6 bg-[#708BA0] hover:bg-[#61a0d0] text-white font-semibold py-2 rounded-xl"
          >
            시작하기
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
