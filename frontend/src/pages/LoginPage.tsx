import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../apis/api";
import useUserStore from "../store/useUserStore";

const LoginPage: React.FC = () => {
  const [formData, setFormData] = useState({
    userId: "",
  });
  const [error, setError] = useState("");
  const navigate = useNavigate();
  // const { setUserSeq, setNotFirstLogin } = useUserStore();

  // 입력값이 변경될 때 formData 업데이트
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // 로그인 버튼 클릭 시 동작
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.userId.trim() === "") {
      setError("일련번호를 입력해주세요.");
    } else {
      setError("");
      try {
        // 로그인 API 호출 (userId만 전달)
        await login({ userId: formData.userId });

        // useUserStore에서 userSeq와 notFirstLogin 값을 가져와서 페이지 이동 결정
        const notFirstLogin = useUserStore.getState().notFirstLogin;

        if (notFirstLogin) {
          navigate("/home");
        } else {
          navigate("/info");
        }
      } catch (error) {
        setError("로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        console.error(error);
      }
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
              placeholder="일련번호"
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
