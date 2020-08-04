import React, {useState} from 'react';
import {Button, Dropdown, Menu} from 'antd';
import {Facet} from 'boclips-api-client/dist/sub-clients/videos/model/VideoFacets';
import DownArrowSvg from './resources/down-arrow.svg';
import UpArrowSvg from './resources/up-arrow.svg';
import s from './styles.module.less';

interface Props {
  options: { [id: string]: Facet };
  title: string;
  onApply: (selected: string[]) => void;
}

const SelectFilter = ({options, title, onApply}: Props) => {
  const [selected, setSelected] = useState<string[]>([]);
  const [visible, setVisible] = useState<boolean>(false);

  const isSelected = (option: string) => {
    return !!selected.find(s => s === option);
  }

  const onClick = (e) => {
    console.log(e);
    if (isSelected(e.key)) {
      setSelected(selected.filter(s => s !== e.key))
    } else {
      setSelected([...selected, e.key])
    }
  };

  const onClear = () => {
    setSelected([]);
    onApply([]);
  }

  const handleVisibleChange = flag => {
    setVisible(flag);
  };

  const onButtonClicked = () => {
    console.log(selected);
    onApply(selected);
    setVisible(false);
  }

  const menuContent = () => (
    <Menu
      className={s.selectWrapper}
          onClick={(e) => onClick(e)}
    >
      {Object.keys(options).map(option => (
        <Menu.Item key={option}>
          <input type="checkbox" key={`checkbox-${option}`} checked={!!selected.find(s => s === option)}/>
          <span>{option}</span>
        </Menu.Item>
      ))}
      <hr />
      <div className={s.buttonRow}>
      <a onClick={onClear}>CLEAR</a>
      <Button onClick={onButtonClicked}>Apply</Button>
      </div>
    </Menu>
  );

  return (
    <Dropdown
      overlay={menuContent}
      trigger={['click']}
      placement="bottomRight"
      onVisibleChange={handleVisibleChange}
      visible={visible}
      className={s.selectWrapper}
    >
      <div
      className={s.selectTitle}
        onClick={() => handleVisibleChange(!visible)}>
      <span>{title}</span>
        {visible ? <UpArrowSvg/> : <DownArrowSvg/>}
      </div>
    </Dropdown>
  );


  // return (
  //   <Select
  //     onChange={(value) => {
  //       console.log(value);
  //     }}
  //     showArrow
  //     options={values.map((it) => ({
  //       label: <Checkbox> {it} </Checkbox>,
  //       value: it,
  //     }))}
  //     placeholder="Ages"
  //     data-qa="select-dropdown"
  //     className={s.selectWrapper}
  //     labelInValue={true}
  //     defaultValue={"Ages"}
  //     // value={{ value: '', label: <div>Ages: {selectedCount}</div> }}
  //     mode="tags"
  //     showSearch={false}
  //     onSelect={(e) => {
  //       console.log(e);
  //     }}
  //     onClick={(e) => {
  //       console.log(e);
  //     }}
  //     dropdownRender={(i) => {
  //       return (
  //         <div>
  //           {i}
  //           <hr />
  //           <Button onClick={onClick}> Apply </Button>
  //         </div>
  //       );
  //     }}
  //   />
  // );
};

export default SelectFilter;
