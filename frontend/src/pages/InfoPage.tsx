import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const InfoPage: React.FC = () => {
  const [formData, setFormData] = useState({
    name: "",
    birthDate: "",
    gender: "",
    favoriteColor: "",
    notes: "",
  });
  const [errorField, setErrorField] = useState(""); // 에러가 발생한 필드만 저장
  const navigate = useNavigate();

  // 입력값이 변경될 때 formData 업데이트
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrorField(""); // 입력 시 에러 메시지 초기화
  };

  // 폼 제출 시 동작
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.name.trim()) {
      setErrorField("name");
    } else if (!formData.birthDate.trim()) {
      setErrorField("birthDate");
    } else if (!formData.gender.trim()) {
      setErrorField("gender");
    } else if (!formData.favoriteColor.trim()) {
      setErrorField("favoriteColor");
    } else {
      setErrorField("");
      navigate("/home"); // TODO
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="bg-white bg-opacity-80 rounded-xl shadow-lg text-center w-2/5 px-28 py-12">
        <h2 className="text-[35px] font-bold mb-6">우리 아이 정보 입력</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            {/* 이름 입력 */}
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="이름"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
              style={{ boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)" }}
            />
            <p className="text-red-500 text-xs text-left ml-2 h-1">
              {errorField === "name" ? "이름을 입력해주세요." : ""}
            </p>
          </div>

          <div className="flex space-x-4">
            {/* 생년월일 입력 */}
            <div className="flex-1">
              <input
                type="date"
                name="birthDate"
                value={formData.birthDate}
                onChange={handleChange}
                placeholder="생년월일"
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF] placeholder-gray-400"
                style={{
                  boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)",
                  color: "inherit",
                }}
              />
              <style>
                {`
                  input[type="date"]::placeholder {
                    color: #a0aec0; /* 회색 */
                  }
                `}
              </style>
            </div>

            {/* 성별 선택 */}
            <div className="flex-1 flex space-x-2">
              <label className="flex items-center flex-1">
                <input
                  type="radio"
                  name="gender"
                  value="남성"
                  checked={formData.gender === "남성"}
                  onChange={handleChange}
                  className="hidden peer"
                />
                <span
                  className={`peer-checked:bg-[#869FD3] peer-checked:text-white bg-[#D7E5EF] w-full text-gray-700 py-2 px-4 rounded-lg cursor-pointer text-center shadow-md`}
                >
                  남성
                </span>
              </label>
              <label className="flex items-center flex-1">
                <input
                  type="radio"
                  name="gender"
                  value="여성"
                  checked={formData.gender === "여성"}
                  onChange={handleChange}
                  className="hidden peer"
                />
                <span
                  className={`peer-checked:bg-[#869FD3] peer-checked:text-white bg-[#D7E5EF] w-full text-gray-700 py-2 px-4 rounded-lg cursor-pointer text-center shadow-md`}
                >
                  여성
                </span>
              </label>
            </div>
          </div>

          {/* 생년월일 및 성별 선택 에러 메시지 */}
          <p className="text-red-500 text-xs text-left ml-2 h-1">
            {errorField === "birthDate" && "생년월일을 입력해주세요."}
            {errorField === "gender" && "성별을 선택해주세요."}
          </p>

          <div className="space-y-2">
            {/* 좋아하는 색 입력 */}
            <input
              type="text"
              name="favoriteColor"
              value={formData.favoriteColor}
              onChange={handleChange}
              placeholder="좋아하는 색"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
              style={{ boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)" }}
            />
            <p className="text-red-500 text-xs text-left ml-2 h-1">
              {errorField === "favoriteColor"
                ? "좋아하는 색을 입력해주세요."
                : ""}
            </p>
          </div>

          <div className="space-y-2">
            {/* 특이사항 입력 (필수 아님) */}
            <input
              type="text"
              name="notes"
              value={formData.notes}
              onChange={handleChange}
              placeholder="특이사항"
              className="w-full px-4 py-2 mb-6 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
              style={{ boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)" }}
            />
          </div>

          <button
            type="submit"
            className="w-full bg-[#708BA0] hover:bg-[#61a0d0] text-white font-semibold py-2 rounded-xl "
          >
            입력 완료
          </button>
        </form>
      </div>
    </div>
  );
};

export default InfoPage;
