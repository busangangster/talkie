import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { updateUserInfo, getUserInfo } from "../apis/api";
import useUserStore from "../store/useUserStore";
import { formatBirthDate } from "../utils/formatBirthDate";
import AlertModal from "../components/AlertModal";
import Image from "../components/Image";

const InfoPage: React.FC = () => {
  const [formData, setFormData] = useState({
    name: "",
    birth: "",
    gender: "",
    favorite: "",
    remark: "",
  });
  const [errorField, setErrorField] = useState("");
  const navigate = useNavigate();
  const { userSeq, setUserInfo } = useUserStore();

  // 유저 정보를 가져와서 formData에 설정
  useEffect(() => {
    const fetchUserInfo = async () => {
      if (userSeq) {
        try {
          const userInfo = await getUserInfo(userSeq);
          setFormData({
            name: userInfo.name,
            birth: formatBirthDate(userInfo.birth),
            gender: userInfo.gender === "M" ? "남성" : "여성",
            favorite: userInfo.favorite,
            remark: userInfo.remark,
          });
          setUserInfo(userInfo);
        } catch (error) {
          console.error("유저 정보를 불러오는 중 오류 발생:", error);
        }
      }
    };
    fetchUserInfo();
  }, [userSeq, setUserInfo]);

  // 입력 필드 변경 핸들러
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  const { name, value } = e.target;
  setFormData((prevData) => ({ ...prevData, [name]: value }));
  setErrorField("");
  };
  const [showModal, setShowModal] = useState(false);

  // 폼 제출 핸들러
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!userSeq) {
      console.error("userSeq가 설정되지 않았습니다.");
      return;
    }

    // 입력 값 검증
    if (!formData.name.trim()) {
      setErrorField("name");
    } else if (!formData.gender.trim()) {
      setErrorField("gender");
    } else if (!formData.favorite.trim()) {
      setErrorField("favorite");
    } else {
      setErrorField("");

      try {
        const genderCode = formData.gender === "남성" ? "M" : "F";

        // API로 업데이트 요청
        const response = await updateUserInfo({
          userSeq,
          ...formData,
          gender: genderCode,
        });

        if (response) {
          // 정보 수정 성공 시 모달 표시
          setShowModal(true);
        }
      } catch (error) {
        console.error("API 요청 중 오류 발생:", error);
      }
    }
  };

  // 모달 확인 버튼 클릭 시 페이지 이동
  const handleModalConfirm = () => {
    setShowModal(false);
    navigate("/home");
  };

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="bg-white bg-opacity-80 rounded-xl shadow-lg text-center w-2/5 px-28 py-12">
        <h2 className="text-[35px] font-bold mb-6">우리 아이 정보 입력</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* 이름 입력 */}
          <div className="space-y-2">
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="이름"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
            />
            <p className="text-red-500 text-xs text-left ml-2 h-1">
              {errorField === "name" ? "이름을 입력해주세요." : ""}
            </p>
          </div>

          {/* 생년월일 입력 */}
          <div className="space-y-2">
            <input
              type="date"
              name="birth"
              value={formData.birth}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
            />
            <p className="text-red-500 text-xs text-left ml-2 h-1">
              {errorField === "birth" ? "생년월일을 입력해주세요." : ""}
            </p>
          </div>

          {/* 성별 선택 */}
          <div className="flex space-x-4">
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
                  className={`peer-checked:bg-[#869FD3] peer-checked:text-white bg-[#D7E5EF] w-full hover:bg-[#B9DCF8] text-gray-700 py-2 px-4 rounded-lg cursor-pointer text-center shadow-md`}
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
                  className={`peer-checked:bg-[#869FD3] peer-checked:text-white bg-[#D7E5EF] w-full hover:bg-[#B9DCF8] text-gray-700 py-2 px-4 rounded-lg cursor-pointer text-center shadow-md`}
                >
                  여성
                </span>
              </label>
            </div>
          </div>

          {/* 좋아하는 것 입력 */}
          <div className="space-y-2">
            <input
              type="text"
              name="favorite"
              value={formData.favorite}
              onChange={handleChange}
              placeholder="좋아하는 것"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
            />
          </div>

          {/* 특이사항 입력 */}
          <div className="space-y-2">
            <input
              type="text"
              name="remark"
              value={formData.remark}
              onChange={handleChange}
              placeholder="특이사항"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
            />
          </div>

          <button
            type="submit"
            className="w-full bg-[#708BA0] hover:bg-[#61a0d0] text-white font-semibold py-2 rounded-xl"
          >
            입력 완료
          </button>
        </form>
      </div>
      {/* AlertModal */}
      {showModal && (
        <AlertModal
          icon={<Image src="/assets/alerticon/check" alt="check" />}
          message="정보가 입력되었어요"
          onConfirm={handleModalConfirm}
        />
      )}
    </div>
  );
};

export default InfoPage;
